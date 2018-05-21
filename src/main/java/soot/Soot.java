package soot;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import soot.handler.FluidFixHandler;
import soot.handler.GolemHandler;
import soot.handler.VillagerAntimonyHandler;
import soot.recipe.CraftingRegistry;
import soot.util.Attributes;
import teamroots.embers.RegistryManager;

@Mod(modid = Soot.MODID, acceptedMinecraftVersions = "[1.12, 1.13)", dependencies = "required-after:embers")
@Mod.EventBusSubscriber
public class Soot
{
    @Mod.Instance(Soot.MODID)
    public static Soot instance;

    public static final String MODID = "soot";
    public static final String NAME = "Soot";

    public static Logger log;

    @SidedProxy(clientSide = "soot.ClientProxy",serverSide = "soot.ServerProxy")
    public static IProxy proxy;

    public static CreativeTabs creativeTab;

    @EventHandler
    public void construct(FMLConstructionEvent event)
    {
        proxy.registerResourcePack();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        Config.preInit(event);
        creativeTab = new CreativeTabs("soot") {
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(RegistryManager.dust_ash);
            }
        };
        CraftingRegistry.preInit();
        Registry.preInit();
        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(Attributes.class);
        MinecraftForge.EVENT_BUS.register(FluidFixHandler.class);
        if(Config.TRADING_ANTIMONY)
            MinecraftForge.EVENT_BUS.register(VillagerAntimonyHandler.class);
        if(Config.GOLEMS_POISON_IMMUNE || Config.GOLEMS_TYRFING_WEAK)
            MinecraftForge.EVENT_BUS.register(GolemHandler.class);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Registry.init();
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
