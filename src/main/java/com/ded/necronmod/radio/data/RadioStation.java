package com.ded.necronmod.radio.data;

import java.util.List;

public record RadioStation(String id, String name, String region, String genre,
                           String tier, List<String> urls, float noise) {}
