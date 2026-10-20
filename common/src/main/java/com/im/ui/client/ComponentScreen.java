package com.im.ui.client;

import com.im.core.menu.ComponentMenu;
import com.im.ui.GuiArea;
import com.im.ui.UIDocument;
import com.im.ui.UIToggle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ComponentScreen extends AbstractContainerScreen<ComponentMenu>
{
    private static final int InventoryLabelGap = 12;

    private final UIDocument _document;
    private final GuiGraphicsDrawer2D _painter = new GuiGraphicsDrawer2D();

    public ComponentScreen(ComponentMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        _document = menu.getEntity().get(UIDocument.class);
        imageWidth = _document.getBackground().width();
        imageHeight = _document.getBackground().height();
        inventoryLabelX = ComponentMenu.InventoryX;
        inventoryLabelY = ComponentMenu.InventoryY - InventoryLabelGap;
        _document.getToggles().forEach(UIToggle::close);
    }

    @Override protected void init()
    {
        super.init();

        for (UIToggle toggle : _document.getToggles())
        {
            GuiArea area = toggle.getArea();
            addRenderableWidget(Button.builder(toggle.getLabel(), button -> toggle.flip()).bounds(leftPos + area.x(), topPos + area.y(), area.width(), area.height()).build());
        }
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        _painter.begin(graphics).drawSprite(_document.getBackground(), leftPos, topPos);
        _document.render(_painter, leftPos, topPos);
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        Component tooltip = _document.getTooltip(mouseX - leftPos, mouseY - topPos);

        if (tooltip != null) graphics.renderTooltip(font, tooltip, mouseX, mouseY);
    }

    @Override protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton)
    {
        return !_document.isOverElement(mouseX - guiLeft, mouseY - guiTop) && super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
    }
}
