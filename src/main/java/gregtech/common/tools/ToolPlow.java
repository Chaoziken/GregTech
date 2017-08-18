package gregtech.common.tools;

import gregtech.api.items.toolitem.ToolMetaItem;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ToolPlow extends ToolBase {

    private ThreadLocal<Object> sIsHarvestingRightNow = new ThreadLocal();

    @Override
    public float getNormalDamageBonus(EntityLivingBase entity, ItemStack stack, EntityLivingBase attacker) {
        return (entity instanceof EntitySnowman) ? 4.0F : 1.0F;
    }

    @Override
    public boolean isMinableBlock(IBlockState block, ItemStack stack) {
        String tTool = block.getBlock().getHarvestTool(block);
        return ((tTool != null) && (tTool.equals("plow"))) ||
                (block.getMaterial() == Material.SNOW) ||
                (block.getMaterial() == Material.CRAFTED_SNOW);
    }

    @Override
    public int convertBlockDrops(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer harvester, List<ItemStack> drops) {
        int rConversions = 0;
        ItemStack stack = harvester.getHeldItem(EnumHand.MAIN_HAND);
        if ((this.sIsHarvestingRightNow.get() == null) && ((harvester instanceof EntityPlayerMP))) {
            this.sIsHarvestingRightNow.set(this);
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        BlockPos block = blockPos.add(i, j, k);
                        IBlockState state = harvester.worldObj.getBlockState(block);
                        if (((i != 0) || (j != 0) || (k != 0)) &&
                                stack.getStrVsBlock(state) > 0.0F &&
                                ((EntityPlayerMP) harvester).interactionManager.tryHarvestBlock(block)) {
                              rConversions++;
                        }
                    }
                }
            }
            this.sIsHarvestingRightNow.set(null);
        }
        return rConversions;
    }

    @Override
    public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? ToolMetaItem.getPrimaryMaterial(aStack).mIconSet.mTextures[OrePrefixes.toolHeadPlow.mTextureIndex] : ToolMetaItem.getSecondaryMaterial(aStack).mIconSet.mTextures[OrePrefixes.stick.mTextureIndex];
    }
}