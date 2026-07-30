package com.ded.necronmod.anekdot;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class NativeTTS {

    private static Process currentProcess = null;

    public static synchronized void speakRussian(String text) {
        // Если уже что-то озвучивается — останавливаем прошлую озвучку
        stopSpeech();

        if (text == null || text.isBlank()) return;

        String cleanText = text
                .replace("\r", "")
                .replace("\n", " ")
                .replace("\"", "'")
                .trim();

        String base64Text = Base64.getEncoder().encodeToString(cleanText.getBytes(StandardCharsets.UTF_8));

        String psScript = String.format(
                "$bytes = [System.Convert]::FromBase64String('%s'); " +
                        "$text = [System.Text.Encoding]::UTF8.GetString($bytes); " +
                        "Add-Type -AssemblyName System.Speech; " +
                        "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "try { $synth.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::Neutral, [System.Speech.Synthesis.VoiceAge]::Adult, 0, [System.Globalization.CultureInfo]::GetCultureInfo('ru-RU')); } catch {} " +
                        "$synth.Speak($text);",
                base64Text
        );

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "powershell.exe",
                        "-NoProfile",
                        "-NonInteractive",
                        "-ExecutionPolicy", "Bypass",
                        "-Command", psScript
                );

                synchronized (NativeTTS.class) {
                    currentProcess = pb.start();
                }

                currentProcess.waitFor();
            } catch (Exception ignored) {
                // Игнорируем прерывание процесса
            } finally {
                synchronized (NativeTTS.class) {
                    currentProcess = null;
                }
            }
        }, "TTS-Speaker-Thread").start();
    }

    // Метод для экстренной остановки озвучки
    public static synchronized void stopSpeech() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
            currentProcess = null;
        }
    }
}