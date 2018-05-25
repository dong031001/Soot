package soot.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    @SideOnly(Side.CLIENT)
    public static void addPotionEffectTooltip(List<PotionEffect> list, List<String> lores, float durationFactor)
    {
        List<Tuple<String, AttributeModifier>> attributeModifiers = Lists.newArrayList();

        if (list.isEmpty())
        {
            String s = I18n.translateToLocal("effect.none").trim();
            lores.add(TextFormatting.GRAY + s);
        }
        else
        {
            for (PotionEffect potioneffect : list)
            {
                String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
                Potion potion = potioneffect.getPotion();
                Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

                if (!map.isEmpty())
                {
                    for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet())
                    {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        attributeModifiers.add(new Tuple(((IAttribute)entry.getKey()).getName(), attributemodifier1));
                    }
                }

                if (potioneffect.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if (potioneffect.getDuration() > 20)
                {
                    s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, durationFactor) + ")";
                }

                if (potion.isBadEffect())
                {
                    lores.add(TextFormatting.RED + s1);
                }
                else
                {
                    lores.add(TextFormatting.BLUE + s1);
                }
            }
        }

        if (!attributeModifiers.isEmpty())
        {
            lores.add("");
            lores.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));

            for (Tuple<String, AttributeModifier> tuple : attributeModifiers)
            {
                AttributeModifier attributemodifier2 = tuple.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
                {
                    d1 = attributemodifier2.getAmount();
                }
                else
                {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    lores.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
                else if (d0 < 0.0D)
                {
                    d1 = d1 * -1.0D;
                    lores.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
            }
        }
    }

    public static void damageWithoutInvulnerability(Entity entity, DamageSource source, float amount)
    {
        if(entity.attackEntityFrom(source,amount))
            entity.hurtResistantTime = 0;
    }

    public static void degradeEquipment(EntityLivingBase entity, int amt) {
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if(armor.isItemStackDamageable())
                armor.damageItem(amt, entity);
        }
    }

    public static boolean isPhysicalDamage(DamageSource damageSource)
    {
        return damageSource.getImmediateSource() != null && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isFireDamage() && !damageSource.isMagicDamage() && !damageSource.isDamageAbsolute();
    }

    public static boolean isBarehandedDamage(DamageSource damageSource, EntityLivingBase attacker)
    {
        return attacker.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty() && isPhysicalDamage(damageSource);
    }

    public static boolean isEitrDamage(DamageSource damageSource, EntityLivingBase attacker)
    {
        return attacker.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() == Registry.EITR && isPhysicalDamage(damageSource);
    }

    public static void setLore(List<String> addedLore, NBTTagCompound compound, boolean append) {
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            NBTTagList lore;
            if(display.hasKey("Lore",9) && append)
                lore = display.getTagList("Lore", 8);
            else {
                lore = new NBTTagList();
                display.setTag("Lore",lore);
            }
            for(String loreString : addedLore)
                lore.appendTag(new NBTTagString(loreString));
        }
    }

    public static List<String> getLore(NBTTagCompound compound) {
        ArrayList<String> addedLore = new ArrayList<>();
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            if(display.hasKey("Lore",9)) {
                NBTTagList lore = display.getTagList("Lore",8);
                if(!lore.hasNoTags())
                    for (int i = 0; i < lore.tagCount(); ++i)
                        addedLore.add(lore.getStringTagAt(i));
            }
        }
        return addedLore;
    }
}
