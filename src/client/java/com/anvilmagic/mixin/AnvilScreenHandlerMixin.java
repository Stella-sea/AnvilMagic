package com.anvilmagic.mixin;

import com.anvilmagic.AnvilMagicClient;
import com.anvilmagic.util.SplittingState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    
    @Shadow @Final private Property levelCost;
    
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
        try {
            AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
            ItemStack leftStack = handler.getSlot(0).getStack();
            ItemStack rightStack = handler.getSlot(1).getStack();
            
            // 如果刚刚完成拆分，跳过这次更新，避免清空结果
            if (SplittingState.checkAndResetSplittingFlag()) {
                return;
            }
            
            // 检查是否为拆分附魔的操作
            if (shouldSplitEnchantedBook(leftStack, rightStack)) {
                handleEnchantmentSplitting(handler, leftStack);
                ci.cancel();
            }
        } catch (Exception e) {
            AnvilMagicClient.LOGGER.error("Error in AnvilMagic updateResult: ", e);
            // 出错时不取消原版逻辑，确保铁砧还能正常工作
        }
    }
    
    private boolean shouldSplitEnchantedBook(ItemStack leftStack, ItemStack rightStack) {
        // 通用拆附魔检测 - 支持所有可附魔物品
        boolean leftHasEnchantments = !EnchantmentHelper.getEnchantments(leftStack).isEmpty();
        boolean rightBook = rightStack.isOf(Items.BOOK); // 右槽必须是普通书
        boolean leftIsNotEnchantedBook = !leftStack.isOf(Items.ENCHANTED_BOOK);
        
        // 检查是否为附魔书拆分（原有功能）
        boolean enchantedBookSplitting = leftStack.isOf(Items.ENCHANTED_BOOK) && leftHasEnchantments && rightBook;
        
        // 检查是否为工具拆附魔（新功能）
        boolean toolEnchantmentSplitting = leftIsNotEnchantedBook && leftHasEnchantments && rightBook;
        
        return enchantedBookSplitting || toolEnchantmentSplitting;
    }
    
    private void handleEnchantmentSplitting(AnvilScreenHandler handler, ItemStack sourceStack) {
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(sourceStack);
        Set<RegistryEntry<Enchantment>> enchantmentSet = enchantments.getEnchantments();
        
        boolean isToolSplitting = !sourceStack.isOf(Items.ENCHANTED_BOOK);
        String itemType = isToolSplitting ? "工具" : "附魔书";
        
        // 附魔书和工具的拆分规则不同
        if (isToolSplitting) {
            // 工具拆附魔：任何附魔数量都可以拆分（包括单个附魔），因为会留下无附魔工具
        } else {
            // 附魔书拆分：只有多个附魔才能拆分，单个附魔不允许（因为会完全消失）
            if (enchantmentSet.size() <= 1) {
                handler.getSlot(2).setStack(ItemStack.EMPTY);
                this.levelCost.set(0);
                return;
            }
        }
        
        // 创建新的附魔书，只包含第一个附魔（仅用于预览结果槽）
        ItemStack resultEnchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        RegistryEntry<Enchantment> firstEnchantment = enchantmentSet.iterator().next();
        int level = enchantments.getLevel(firstEnchantment);
        
        ItemEnchantmentsComponent.Builder resultBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        resultBuilder.add(firstEnchantment, level);
        EnchantmentHelper.set(resultEnchantedBook, resultBuilder.build());
        
        // 设置结果槽（仅预览，不修改左槽）
        handler.getSlot(2).setStack(resultEnchantedBook);
        
        // 设置经验消耗为1级
        this.levelCost.set(1);
        
        AnvilMagicClient.LOGGER.info("准备拆分{}:  {} -> 1 (预览)", 
            itemType, enchantmentSet.size());
    }
}