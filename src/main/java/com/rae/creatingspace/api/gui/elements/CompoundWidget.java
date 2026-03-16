package com.rae.creatingspace.api.gui.elements;


import net.createmod.catnip.gui.TickableGuiEventListener;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;

import java.util.ArrayList;
import java.util.List;

/**
 * A widget that can **contain other widgets**, providing a modular way
 * to compose complex GUI screens.
 * <p>
 * This class manages child widgets and handles:
 * <ul>
 *     <li>Rendering all children in proper Z order.</li>
 *     <li>Ticking/updating all children each frame.</li>
 *     <li>Propagating hover and click events to children.</li>
 *     <li>Optional layout management for automatic positioning.</li>
 * </ul>
 * <p>
 * Child widgets are stored in a list, and the order of rendering is determined
 * by the {@link AbstractSimiWidget#z} property (higher Z is rendered on top).
 * <p>
 * Example usage:
 * <pre>
 *     CompoundedWidget container = new MyContainerWidget(10, 20);
 *     container.addWidget(new ButtonWidget(0, 0).withCallback(() -> System.out.println("Clicked")));
 * </pre>
 */
public abstract class CompoundWidget extends AbstractSimiWidget {

    /** The list of child widgets contained in this widget. */
    protected final List<AbstractWidget> children = new ArrayList<>();
    protected AbstractWidget focused;

    /**
     * Constructs a new CompoundedWidget at the given position.
     *
     * @param x The X coordinate of this widget.
     * @param y The Y coordinate of this widget.
     */
    protected CompoundWidget(int x, int y) {
        super(x, y);
    }

    /**
     * Adds a child widget to this container.
     *
     * @param widget The widget to add.
     * @return This container for chaining.
     */
    public <T extends CompoundWidget> T addWidget(AbstractWidget widget) {
        children.add(widget);
        return (T) this;
    }

    /**
     * Removes a child widget from this container.
     *
     * @param widget The widget to remove.
     * @return This container for chaining.
     */
    public <T extends CompoundWidget> T removeWidget(AbstractWidget widget) {
        children.remove(widget);
        return (T) this;
    }

    /**
     * Updates this widget and all children.
     */
    @Override
    public void tick() {
        for (AbstractWidget child : children) {
            if (child.visible ) {
                if (child instanceof TickableGuiEventListener tickable) {
                    tickable.tick();
                } else if (child instanceof EditBox editBox) {
                    editBox.tick();
                }

            }
        }
    }

    // ------------------------
    // EVENT PROPAGATION
    // ------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (focused != null) {
            focused.setFocused(false);//clear the focuse first
            focused = null;
        }
        for (int i = children.size() - 1; i >= 0; i--) {

            AbstractWidget child = children.get(i);

            if (child.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(child); // needed for EditBox
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (AbstractWidget child : children) {

            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        for (AbstractWidget child : children) {

            if (!child.visible)
                continue;

            if (!child.isMouseOver(mouseX, mouseY))
                continue;

            if (child.mouseScrolled(mouseX, mouseY, delta))
                return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        for (AbstractWidget child : children) {
            if (child.keyPressed(key, scanCode, modifiers)) {
                return true;
            }
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private void setFocused(AbstractWidget widget) {

        widget.setFocused(true);
        focused = widget;
    }

    @Override
    public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) {
        return super.mouseDragged(p_93645_, p_93646_, p_93647_, p_93648_, p_93649_);
    }

    @Override
    public void mouseMoved(double p_94758_, double p_94759_) {
        super.mouseMoved(p_94758_, p_94759_);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {

        if (focused != null)
            return focused.charTyped(character, modifiers);


        return false;
    }
    /*@Override
    public boolean charTyped(char character, int modifiers) {
        boolean flag = false;
        for (AbstractWidget child : children) {
            //let everyone get it -> if one consume a character then return true else false.
            flag = child.charTyped(character, modifiers) || flag;
        }

        return flag;
    }*/

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) return true;

        for (AbstractWidget child : children) {
            if (child.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Renders this widget and all children, respecting Z order.
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        // Update hover state for this container
        isHovered = isMouseOver(mouseX, mouseY);

        // Call the standard render flow
        beforeRender(graphics, mouseX, mouseY, partialTicks);
        doRender(graphics, mouseX, mouseY, partialTicks);

        // We can't sort by Z, because z is protected
        // Sort children by Z before rendering (lowest first)

        afterRender(graphics, mouseX, mouseY, partialTicks);
        children.stream()
                //.filter(child -> child.visible)
                //.sorted(Comparator.comparingDouble(child -> child.z))
                .forEach(child -> child.render(graphics, mouseX, mouseY, partialTicks));
        wasHovered = isHoveredOrFocused();
    }

    /**
     * Handles clicks and propagates to children.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        // First, check children (topmost first)
        children.stream()
                .filter(AbstractWidget::isActive)
                //.sorted((a, b) -> Float.compare(b.z, a.z)) // reverse order: topmost first
                .filter(child -> child.isMouseOver(mouseX, mouseY))
                .findFirst()
                .ifPresentOrElse(
                        child -> child.onClick(mouseX, mouseY),
                        () -> runCallback(mouseX, mouseY) // fallback to this widget
                );
    }

    /**
     * Returns true if this widget or any child is hovered.
     */
    @Override
    public boolean isHoveredOrFocused() {
        if (super.isHoveredOrFocused()) return true;
        return children.stream().anyMatch(AbstractWidget::isHoveredOrFocused);
    }

    /**
     * Optional method to layout children automatically.
     * Override this in subclasses to define custom layouts
     * (e.g., vertical/horizontal stacking).
     */
    protected void layoutChildren() {
        // Example: simple vertical layout (override in subclass)
        int offsetY = 0;
        for (AbstractWidget child : children) {
            child.setY(this.getY() + offsetY);
            child.setX(this.getX());
            offsetY += child.getHeight();
        }
    }
}