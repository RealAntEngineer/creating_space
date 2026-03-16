package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import com.rae.creatingspace.api.gui.elements.CompoundWidget;
import com.rae.creatingspace.api.gui.elements.DecimalScrollInput;
import com.simibubi.create.foundation.gui.widget.Label;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.List;

public class AerospikeWidget extends CompoundWidget {

    private static final int ScrollInputWidth = 66;
    private static final int DRAW_WIDTH = ScrollInputWidth * 3;
    private static final int DRAW_HEIGHT = 80;
    RocketEngineDesign design;
    DecimalScrollInput expansionRatio, truncation, finalFlowAngle;
    private List<Vec2> geometry;

    private double Rt = 0.7;           // throat radius
    private double Ac = 1.0;           // critical area -> derived from thrust
    private double finalMach = 1;
    private double thetaT = Math.toRadians(-80); // throat wall angle -> derived from final angle and expansion ratio (final Mach)


    public AerospikeWidget(int x, int y, RocketEngineDesign design) {
        super(x, y);
        setHeight(120);
        setWidth(198);
        //this.mode = mode;
        this.design = design;
        Label expansionRatioLabel, truncationLabel, finalAngleLabel;

        expansionRatioLabel = new Label(x + 2 + 5, y + 2 + DRAW_HEIGHT + 5, Component.empty()).withShadow();
        truncationLabel = new Label(x + 2 + ScrollInputWidth + 5, y + 2 + DRAW_HEIGHT + 5, Component.empty()).withShadow();
        finalAngleLabel = new Label(x + 2 + ScrollInputWidth * 2 + 5, y + 2 + DRAW_HEIGHT + 5, Component.empty()).withShadow();

        expansionRatio = (DecimalScrollInput) new DecimalScrollInput(x + 2, y + DRAW_HEIGHT + 2, 64, 16, 2).withRange(1.0f, 100.0f).writingTo(expansionRatioLabel);
        truncation = (DecimalScrollInput) new DecimalScrollInput(x + 2 + ScrollInputWidth, y + DRAW_HEIGHT + 2, 64, 16, 2).withRange(0f, 1f).writingTo(truncationLabel);
        finalFlowAngle = (DecimalScrollInput) new DecimalScrollInput(x + 2 + ScrollInputWidth * 2, y + DRAW_HEIGHT + 2, 64, 16, 2).withRange(0f, 90f).writingTo(finalAngleLabel);

        expansionRatio.calling((i ) -> updateGeometry())
                .withStepFunction(sc -> sc.shift ? 100 : 1);
        truncation.calling((i ) -> updateGeometry())
                .withStepFunction(sc -> sc.shift ? 100 : 1);
        finalFlowAngle.calling((i ) -> updateGeometry())
                .withStepFunction(sc -> sc.shift ? 100 : 1);

        expansionRatio.setDecimalValue(10);
        expansionRatio.onChanged();
        truncation.setDecimalValue(1);
        truncation.onChanged();
        finalFlowAngle.setDecimalValue(0);
        finalFlowAngle.onChanged();


        addWidget(expansionRatio);
        addWidget(truncation);
        addWidget(finalFlowAngle);
        addWidget(expansionRatioLabel);
        addWidget(truncationLabel);
        addWidget(finalAngleLabel);

        //geometry = AerospikeGeometry.generateExpansion(Rt, Ac, thetaT, finalFlowAngle.get() / 180 * Math.PI, 0.1, 200);

    }

    public void updateGeometry() {
        double targetAngle = finalFlowAngle.getDecimalValue() / 180 * Math.PI;

        finalMach = design.getExitMach((float) expansionRatio.getDecimalValue());
        System.out.println("Final Mach: " + finalMach);

        thetaT = -(targetAngle +
                PrandtlMeyer.prandtlMeyer(finalMach, design.propellant.gamma));
        thetaT = Mth.clamp(thetaT, -Math.PI/2, 0);
        System.out.println("Initial angle: " + thetaT);

        double tol = 1e-4;

        for (int i = 0; i < 200; i++) {

            geometry = AerospikeGeometry.generateExpansion(
                    Rt,
                    Ac,
                    thetaT,
                    targetAngle,
                    finalMach / 280,
                    300
            );

            Vec2 last = geometry.get(geometry.size() - 1);

            double diff = last.y;//this needs to be 0

            if (Math.abs(diff) < tol)
                break;

            Rt -= diff * 0.5; // apply negative error correction
        }//it's a fixed point iteration

        System.out.println("Rt: " + Rt);
        System.out.println("geometry length: " + geometry.size());
    }

    @Override
    protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);
        if (geometry == null) updateGeometry();
        renderNozzle(graphics);

        //render drawing area
        graphics.hLine(getX(), getX() + DRAW_WIDTH, getY(), 0xFF444444);
        graphics.hLine(getX(), getX() + DRAW_WIDTH, getY() + DRAW_HEIGHT, 0xFF444444);
        graphics.vLine(getX(), getY(), getY() + DRAW_HEIGHT, 0xFF444444);
        graphics.vLine(getX() + DRAW_WIDTH, getY(), getY() + DRAW_HEIGHT, 0xFF444444);
    }

    private void renderNozzle(GuiGraphics graphics) {
        if (geometry.isEmpty()) return;

        double trunc = truncation.getDecimalValue();
        double maxX = geometry.get(geometry.size() - 1).x * trunc;

        // find max radius in truncated section
        double maxR = 0;
        for (Vec2 p : geometry) {
            if (p.x > maxX) break;
            maxR = Math.max(maxR, Math.abs(p.y));
        }

        if (maxR == 0) return;

        // compute scaling to fit inside drawing box
        double scaleX = DRAW_WIDTH / maxX;
        double scaleY = (DRAW_HEIGHT / 2.0) / maxR;
        double scale = Math.min(scaleX, scaleY);

        int offX = getX();
        int centerY = getY() + DRAW_HEIGHT / 2;

        Vec2 prev = geometry.get(0);

        for (int i = 1; i < geometry.size(); i++) {
            Vec2 p = geometry.get(i);

            // If we cross the truncation plane
            if (p.x > maxX) {

                double t = (maxX - prev.x) / (p.x - prev.x);
                double yCut = prev.y + t * (p.y - prev.y);

                int xCut = offX + (int) (maxX * scale);
                int yTop = centerY - (int) (yCut * scale);
                int yBottom = centerY + (int) (yCut * scale);

                // draw the vertical truncation face
                graphics.vLine(xCut, yTop, yBottom, 0xFFFFFFFF);
                break;
            }

            int x0 = offX + (int) (prev.x * scale);
            int y0 = centerY - (int) (prev.y * scale);

            int x1 = offX + (int) (p.x * scale);
            int y1 = centerY - (int) (p.y * scale);

            drawLineApprox(graphics, x0, y0, x1, y1, 0xFFFFFFFF);

            // mirrored bottom
            drawLineApprox(
                    graphics,
                    x0, centerY + (int) (prev.y * scale),
                    x1, centerY + (int) (p.y * scale),
                    0xFFFFFFFF
            );

            prev = p;
        }
    }

    private void drawLineApprox(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;

        while (true) {
            // draw one pixel
            graphics.hLine(x0, x0, y0, color);

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}