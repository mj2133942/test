package forgefuck.team.xenobyte.modules;

import forgefuck.team.xenobyte.api.Xeno;
import forgefuck.team.xenobyte.api.config.Cfg;
import forgefuck.team.xenobyte.api.gui.WidgetMode;
import forgefuck.team.xenobyte.api.integration.NEI;
import forgefuck.team.xenobyte.api.module.Category;
import forgefuck.team.xenobyte.api.module.CheatModule;
import forgefuck.team.xenobyte.api.module.PerformMode;
import forgefuck.team.xenobyte.api.module.PerformSource;
import forgefuck.team.xenobyte.gui.click.elements.Button;
import forgefuck.team.xenobyte.gui.click.elements.Panel;
import forgefuck.team.xenobyte.gui.click.elements.ScrollSlider;
import forgefuck.team.xenobyte.gui.swing.ItemChanter;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class GiveSelect extends CheatModule {
    
    @Cfg("withChant") private boolean withChant;
    @Cfg("fillSlots") private boolean fillSlots;
    @Cfg("count") private int count;
    private ItemStack givedItem;
    private ItemChanter chanter;

    public GiveSelect() {
        super("GiveSelect", Category.NEI, PerformMode.SINGLE);
        givedItem = new ItemStack(Blocks.command_block);
        chanter = new ItemChanter();
        count = 1;
    }
    
    public ItemStack givedItem() {
        NBTTagCompound chant = givedNBT();
        ItemStack outItem = givedItem.copy();
        return Xeno.utils.item(outItem.splitStack(count), withChant && !chant.hasNoTags() ? chant : null).copy();
    }
    
    public NBTTagCompound givedNBT() {
        return chanter.getOutNBT();
    }
    
    public int itemCount() {
        return count;
    }
    
    public boolean fillAllSlots() {
        return fillSlots;
    }
    
    @Override public void onPerform(PerformSource src) {
        if (src == PerformSource.KEY) {
            ItemStack checkItem = NEI.getStackMouseOver();
            if (checkItem != null) {
                widgetMessage(lang.get("selected ", "???????????? ") + (givedItem = checkItem).getDisplayName() + " [x" + count + "]", WidgetMode.INFO);
            } else if (utils.isInGameGui()) {
                if (chanter.isCheckMode()) {
                    NBTTagCompound outTag = new NBTTagCompound();
                    String outMessage = new String();
                    TileEntity tile = Xeno.utils.tile();
                    Entity entity = Xeno.utils.entity();
                    ItemStack item = Xeno.utils.item();
                    if (item != null) {
                        if (item.hasTagCompound()) {
                            outTag = item.getTagCompound();
                            outMessage = lang.get("loaded NBT of item ", "???????????????? NBT ???????????????? ") + item.getDisplayName();
                        }
                    } else if (tile != null) {
                        tile.writeToNBT(outTag);
                        outMessage = lang.get("loaded NBT of tile ", "???????????????? NBT ?????????? ") + Xeno.utils.formatCoords(tile.xCoord, tile.yCoord, tile.zCoord);
                    } else if (entity != null) {
                        entity.writeToNBT(outTag);
                        outMessage = lang.get("loaded NBT of mob ", "???????????????? NBT ???????? ") + entity.getCommandSenderName();
                    }
                    if (!outTag.hasNoTags()) {
                        widgetMessage(outMessage, WidgetMode.INFO);
                        chanter.loadCustomNBT(outTag);
                    }
                }
                chanter.showFrame();
            }
        } else {
            widgetMessage(lang.get("to select an item, you need to hover over it and press the keybind", "?????? ???????????? ???????????????? ???????????????????? ?????????????? ???? ???????? ?? ???????????? ??????????????"), WidgetMode.FAIL);
        }
    }
    
    @Override public boolean isWorking() {
        return NEI.isAvailable();
    }
    
    @Override public boolean inGuiPerform() {
        return true;
    }
    
    @Override public boolean allowStateMessages() {
        return false;
    }
    
    @Override public String moduleDesc() {
        return lang.get("Selecting an item in NEI by keybind for issuance via exploits", "?????????? ???????????????? ?? NEI ???? ???????????????? ?????? ???????????? ?????????? ??????????????????") + " (" + givedItem.getDisplayName() + ")";
    }
    
    @Override public Panel settingPanel() {
        return new Panel(
            new ScrollSlider("Count", count, 64) {
                @Override public void onScroll(int dir, boolean withShift) {
                    count = processSlider(dir, withShift);
                }
                @Override public String elementDesc() {
                    return lang.get("Count of the issued item", "???????????????????? ?????????????????????? ????????????????");
                }
            },
            new Button("FillSlots", fillSlots) {
                @Override public void onLeftClick() {
                    buttonValue(fillSlots = !fillSlots);
                }
                @Override public String elementDesc() {
                    return lang.get("If possible, fill all the slots in the inventory when issuing", "???? ?????????????????????? ?????????????????? ?????? ?????????? ?? ?????????????????? ?????? ????????????");
                }
            },
            new Button("WithChant", withChant) {
                @Override public void onLeftClick() {
                    buttonValue(withChant = !withChant);
                }
                @Override public String elementDesc() {
                    return lang.get("With NBT from Chanter", "?? ?????????????????????? NBT ???? Chanter");
                }
            },
            new Button("Chanter") {
                @Override public void onLeftClick() {
                    chanter.showFrame();
                }
                @Override public String elementDesc() {
                    return lang.get("NBT editor (will open by GiveSelect keybind)", "???????????????? NBT (?????????????????????? ???? ???????????????? GiveSelect)");
                }
            }
        );
    }

}
