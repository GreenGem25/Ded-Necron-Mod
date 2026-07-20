package com.ded.necronmod.jade;

import com.ded.necronmod.block.NecronTombBlock;
import com.ded.necronmod.block.NecronTombSlabBlock;
import com.ded.necronmod.block.NecronTombStairsBlock;
import com.ded.necronmod.block.NecronTombWallBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerBlockComponent(
                NecronTombBlockProvider.INSTANCE,
                NecronTombBlock.class
        );
        registration.registerBlockComponent(
                NecronTombBlockProvider.INSTANCE,
                NecronTombStairsBlock.class
        );
        registration.registerBlockComponent(
                NecronTombBlockProvider.INSTANCE,
                NecronTombSlabBlock.class
        );
        registration.registerBlockComponent(
                NecronTombBlockProvider.INSTANCE,
                NecronTombWallBlock.class
        );
    }
}