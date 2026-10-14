package com.cloudsim.gui.ui;

/**
 * Styles
 * Central style constants so every view uses the same palette,
 * typography and spacing. Change one value here to update the whole app.
 */
public final class Styles {

    private Styles() {}

    // ── Colour palette ────────────────────────────────────────────────
    public static final String BG_APP        = "#1e2433";   // sidebar background
    public static final String BG_CONTENT    = "#f5f7fa";   // main content area
    public static final String BG_CARD       = "#ffffff";   // card / panel
    public static final String BG_CARD_HOVER = "#f0f4ff";
    public static final String ACCENT        = "#3b82f6";   // primary blue
    public static final String ACCENT_DARK   = "#1d4ed8";
    public static final String SUCCESS       = "#22c55e";
    public static final String WARNING       = "#f59e0b";
    public static final String DANGER        = "#ef4444";
    public static final String TEXT_PRIMARY  = "#1e293b";
    public static final String TEXT_SECONDARY= "#64748b";
    public static final String TEXT_LIGHT    = "#94a3b8";
    public static final String BORDER        = "#e2e8f0";
    public static final String SIDEBAR_TEXT  = "#cbd5e1";
    public static final String SIDEBAR_ACTIVE= "#3b82f6";

    // ── Typography ────────────────────────────────────────────────────
    public static final String FONT_TITLE   = "-fx-font-size:22px;-fx-font-weight:bold;";
    public static final String FONT_H2      = "-fx-font-size:17px;-fx-font-weight:bold;";
    public static final String FONT_H3      = "-fx-font-size:14px;-fx-font-weight:bold;";
    public static final String FONT_BODY    = "-fx-font-size:13px;";
    public static final String FONT_SMALL   = "-fx-font-size:11px;";
    public static final String FONT_CAPTION = "-fx-font-size:10px;";

    // ── Reusable inline styles ────────────────────────────────────────
    public static final String CARD =
        "-fx-background-color:" + BG_CARD + ";" +
        "-fx-background-radius:10;" +
        "-fx-border-color:" + BORDER + ";" +
        "-fx-border-radius:10;" +
        "-fx-border-width:1;" +
        "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);";

    public static final String BTN_PRIMARY =
        "-fx-background-color:" + ACCENT + ";" +
        "-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-font-weight:bold;" +
        "-fx-padding:9 22 9 22;" +
        "-fx-background-radius:7;" +
        "-fx-cursor:hand;";

    public static final String BTN_PRIMARY_HOVER =
        "-fx-background-color:" + ACCENT_DARK + ";" +
        "-fx-text-fill:white;" +
        "-fx-font-size:13px;-fx-font-weight:bold;" +
        "-fx-padding:9 22 9 22;" +
        "-fx-background-radius:7;" +
        "-fx-cursor:hand;";

    public static final String BTN_SUCCESS =
        "-fx-background-color:" + SUCCESS + ";" +
        "-fx-text-fill:white;" +
        "-fx-font-size:14px;-fx-font-weight:bold;" +
        "-fx-padding:11 32 11 32;" +
        "-fx-background-radius:8;" +
        "-fx-cursor:hand;";

    public static final String BTN_OUTLINE =
        "-fx-background-color:transparent;" +
        "-fx-text-fill:" + ACCENT + ";" +
        "-fx-font-size:13px;-fx-font-weight:bold;" +
        "-fx-padding:8 20 8 20;" +
        "-fx-background-radius:7;" +
        "-fx-border-color:" + ACCENT + ";" +
        "-fx-border-radius:7;" +
        "-fx-cursor:hand;";

    public static final String INPUT =
        "-fx-background-color:#f8fafc;" +
        "-fx-border-color:" + BORDER + ";" +
        "-fx-border-radius:6;" +
        "-fx-background-radius:6;" +
        "-fx-padding:7 10 7 10;" +
        "-fx-font-size:13px;";

    public static final String INPUT_ERROR =
        "-fx-background-color:#fff5f5;" +
        "-fx-border-color:" + DANGER + ";" +
        "-fx-border-radius:6;" +
        "-fx-background-radius:6;" +
        "-fx-padding:7 10 7 10;" +
        "-fx-font-size:13px;";

    public static final String SECTION_HEADER =
        "-fx-font-size:13px;-fx-font-weight:bold;" +
        "-fx-text-fill:" + TEXT_SECONDARY + ";" +
        "-fx-padding:0 0 6 0;";

    public static String statCard(String accentColor) {
        return "-fx-background-color:" + BG_CARD + ";" +
               "-fx-background-radius:10;" +
               "-fx-border-color:" + accentColor + ";" +
               "-fx-border-radius:10;" +
               "-fx-border-width:0 0 0 4;" +
               "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),8,0,0,2);";
    }
}