package com.ded.necronmod.jade;

import com.ded.necronmod.block.NecronTombBlock;
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
    }
}