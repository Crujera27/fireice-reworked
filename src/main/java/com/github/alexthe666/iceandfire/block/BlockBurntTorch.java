package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBurntTorch extends TorchBlock implements IDreadBlock, IWallBlock {

    public BlockBurntTorch() {
        super(
            Properties.create(Material.WOOD)
                .setLightLevel((state) -> {
                    return 0;
                })
                .sound(SoundType.WOOD)
                .notSolid()
                .variableOpacity()
                .doesNotBlockMovement(),
            RedstoneParticleData.REDSTONE_DUST
        );

        setRegistryName(IceAndFire.MODID, "burnt_torch");
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

    }

    @Override
    public Block wallBlock() {
        return IafBlockRegistry.BURNT_TORCH_WALL;
    }
}