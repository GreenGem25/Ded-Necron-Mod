package com.ded.necronmod.radio;

import org.jetbrains.annotations.NotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Открывает поток радиостанции: ручные редиректы (в том числе http <-> https),
 * разворот плейлистов .m3u/.pls, вырезание ICY-метаданных, таймауты чтения.
 * Вызывается ТОЛЬКО из фонового потока декодера.
 */
final class RadioHttp {
    private static final int MAX_HOPS = 6;
    private static final String USER_AGENT = "YourRadioMod/1.0 (Minecraft)";
    private static final Pattern URL_IN_TEXT = Pattern.compile("https?://[^\\s\"'<>]+");

    private static final Set<String> PLAYLIST_TYPES = Set.of(
            "audio/x-mpegurl", "audio/mpegurl", "audio/x-scpls", "application/pls+xml");
    private static final Set<String> BAD_TYPES = Set.of(
            "audio/aac", "audio/aacp", "audio/ogg", "application/ogg",
            "application/vnd.apple.mpegurl", "application/x-mpegurl"); // последние два = HLS

    private RadioHttp() {}

    /**
     * @param onConnection вызывается для каждого нового соединения ДО подключения,
     *                     чтобы владелец мог оборвать его через disconnect() из другого потока.
     * @return поток аудиоданных; close() вызывает disconnect() (сокет закрывается сразу).
     */
    static InputStream open(String url, Consumer<HttpURLConnection> onConnection) throws IOException {
        for (int hop = 0; hop < MAX_HOPS; hop++) {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new IOException("unsupported scheme: " + scheme);
            }

            HttpURLConnection c = (HttpURLConnection) uri.toURL().openConnection();
            onConnection.accept(c);
            c.setInstanceFollowRedirects(false);
            c.setConnectTimeout(6_000);
            c.setReadTimeout(10_000); // таймаут на КАЖДОЕ чтение: «молчащий» сервер = ошибка и переподключение
            c.setRequestProperty("User-Agent", USER_AGENT);
            c.setRequestProperty("Icy-MetaData", "0");
            c.setRequestProperty("Accept", "*/*");

            boolean keep = false;
            try {
                int code = c.getResponseCode();
                if (code == 301 || code == 302 || code == 303 || code == 307 || code == 308) {
                    String location = c.getHeaderField("Location");
                    if (location == null) throw new IOException("redirect without Location");
                    url = uri.resolve(location.trim()).toString();
                    continue;
                }
                if (code != 200) throw new IOException("HTTP " + code);

                String type = c.getContentType();
                type = type == null ? "" : type.split(";")[0].trim().toLowerCase(Locale.ROOT);
                String path = uri.getPath() == null ? "" : uri.getPath().toLowerCase(Locale.ROOT);

                if (PLAYLIST_TYPES.contains(type) || path.endsWith(".m3u") || path.endsWith(".pls")) {
                    String text = new String(c.getInputStream().readNBytes(8192), StandardCharsets.UTF_8);
                    if (text.contains("#EXT-X-")) throw new IOException("HLS is not supported");
                    Matcher m = URL_IN_TEXT.matcher(text);
                    if (!m.find()) throw new IOException("empty playlist");
                    url = m.group();
                    continue;
                }
                if (BAD_TYPES.contains(type) || type.startsWith("text/") || type.startsWith("video/")) {
                    throw new IOException("unsupported content type: " + type);
                }

                InputStream in = c.getInputStream();
                int metaInt = parseInt(c.getHeaderField("icy-metaint"));
                if (metaInt > 0) in = new IcyMetadataStripper(in, metaInt);
                keep = true;
                return new DisconnectOnClose(in, c);
            } finally {
                if (!keep) c.disconnect();
            }
        }
        throw new IOException("too many redirects/playlists");
    }

    private static int parseInt(String s) {
        try { return s == null ? 0 : Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
    }

    /** Закрытие потока бесконечного ответа через close() может зависнуть на «дочитывании», поэтому рвём сокет. */
    private static final class DisconnectOnClose extends FilterInputStream {
        private final HttpURLConnection connection;

        DisconnectOnClose(InputStream in, HttpURLConnection connection) {
            super(in);
            this.connection = connection;
        }

        @Override public void close() { connection.disconnect(); }
    }

    /** Если сервер всё же прислал ICY-метаданные (icy-metaint), вырезаем их из аудиопотока. */
    static final class IcyMetadataStripper extends FilterInputStream {
        private final int interval;
        private int untilMeta;

        IcyMetadataStripper(InputStream in, int interval) {
            super(in);
            this.interval = interval;
            this.untilMeta = interval;
        }

        @Override
        public int read() throws IOException {
            byte[] one = new byte[1];
            int n = read(one, 0, 1);
            return n <= 0 ? -1 : one[0] & 0xFF;
        }

        @Override
        public int read(byte @NotNull [] b, int off, int len) throws IOException {
            if (untilMeta == 0) {
                int blocks = in.read();
                if (blocks < 0) return -1;
                if (blocks > 0) in.skipNBytes(blocks * 16L);
                untilMeta = interval;
            }
            int n = in.read(b, off, Math.min(len, untilMeta));
            if (n > 0) untilMeta -= n;
            return n;
        }
    }
}
