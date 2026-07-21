package com.ded.necronmod.tick;

import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import com.ded.necronmod.sound.BrainrotSoundManager;

public class ClientTickHandler {
    public static void tickTower(TowerTopBlockEntity be) {
        BrainrotSoundManager.tick(be);
    }
}
