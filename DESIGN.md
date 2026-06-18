---
name: ZenSpend
colors:
    surface: "#f8f9fa"
    surface-dim: "#d9dadb"
    surface-bright: "#f8f9fa"
    surface-container-lowest: "#ffffff"
    surface-container-low: "#f3f4f5"
    surface-container: "#edeeef"
    surface-container-high: "#e7e8e9"
    surface-container-highest: "#e1e3e4"
    on-surface: "#191c1d"
    on-surface-variant: "#3f484d"
    inverse-surface: "#2e3132"
    inverse-on-surface: "#f0f1f2"
    outline: "#6f787d"
    outline-variant: "#bfc8cd"
    surface-tint: "#026783"
    primary: "#006480"
    on-primary: "#ffffff"
    primary-container: "#2d7d9a"
    on-primary-container: "#fafdff"
    inverse-primary: "#87d0f0"
    secondary: "#516161"
    on-secondary: "#ffffff"
    secondary-container: "#d4e6e5"
    on-secondary-container: "#576867"
    tertiary: "#006b1b"
    on-tertiary: "#ffffff"
    tertiary-container: "#1d862d"
    on-tertiary-container: "#f6ffef"
    error: "#ba1a1a"
    on-error: "#ffffff"
    error-container: "#ffdad6"
    on-error-container: "#93000a"
    primary-fixed: "#bce9ff"
    primary-fixed-dim: "#87d0f0"
    on-primary-fixed: "#001f29"
    on-primary-fixed-variant: "#004d63"
    secondary-fixed: "#d4e6e5"
    secondary-fixed-dim: "#b8cac9"
    on-secondary-fixed: "#0e1e1e"
    on-secondary-fixed-variant: "#3a4a49"
    tertiary-fixed: "#94f990"
    tertiary-fixed-dim: "#78dc77"
    on-tertiary-fixed: "#002204"
    on-tertiary-fixed-variant: "#005313"
    background: "#f8f9fa"
    on-background: "#191c1d"
    surface-variant: "#e1e3e4"
typography:
    display-amount:
        fontFamily: Inter
        fontSize: 40px
        fontWeight: "600"
        lineHeight: 48px
        letterSpacing: -0.02em
    headline-lg:
        fontFamily: Inter
        fontSize: 32px
        fontWeight: "600"
        lineHeight: 40px
        letterSpacing: -0.01em
    headline-lg-mobile:
        fontFamily: Inter
        fontSize: 24px
        fontWeight: "600"
        lineHeight: 32px
    title-md:
        fontFamily: Inter
        fontSize: 18px
        fontWeight: "500"
        lineHeight: 24px
    body-lg:
        fontFamily: Inter
        fontSize: 16px
        fontWeight: "400"
        lineHeight: 24px
    body-md:
        fontFamily: Inter
        fontSize: 14px
        fontWeight: "400"
        lineHeight: 20px
    label-sm:
        fontFamily: Inter
        fontSize: 12px
        fontWeight: "500"
        lineHeight: 16px
        letterSpacing: 0.05em
    numeric-data:
        fontFamily: Inter
        fontSize: 16px
        fontWeight: "500"
        lineHeight: 24px
rounded:
    sm: 0.25rem
    DEFAULT: 0.5rem
    md: 0.75rem
    lg: 1rem
    xl: 1.5rem
    full: 9999px
spacing:
    base: 8px
    xs: 4px
    sm: 8px
    md: 16px
    lg: 24px
    xl: 32px
    container-padding: 20px
    gutter: 16px
---

## Brand & Style

This design system is built upon the intersection of **Material Design 3 structural logic** and **Japanese minimalism**. The primary objective is to transform the often-stressful task of expense tracking into a meditative, calm experience. The interface prioritizes clarity, breathability, and intentionality.

The aesthetic follows a **Modern Corporate** approach with a **Minimalist** lean. It utilizes generous whitespace, a restricted but purposeful color palette, and subtle tactile cues to establish a premium, trustworthy atmosphere. The user should feel in control and at ease, moving through the app with a sense of "digital flow." Interactions are soft and fluid, avoiding jarring transitions to maintain the "Zen" state.

## Colors

The palette is anchored by a calming blue-teal, designed to evoke stability and focus.

- **Primary (#2D7D9A):** Used for key actions, active states, and branding elements.
- **Secondary (#E0F2F1):** A soft teal for container backgrounds and subtle accents that reduce visual fatigue.
- **Surface & Background:** An off-white (#F8F9FA) base keeps the UI soft, while pure white (#FFFFFF) surfaces are used to denote elevation and focus areas.
- **Semantic Logic:** Income uses a balanced green, Expenses use a soft coral (avoiding aggressive reds), and Warnings use a warm amber. These colors should be used with high transparency in backgrounds to maintain the "Zen" aesthetic.

## Typography

The typography system relies on **Inter** for its exceptional legibility and neutral, modern character.

A critical requirement for this system is the use of **Tabular Numbers** (`tnum`) for all monetary values. This ensures that the Indonesian Rupiah (Rp) values align perfectly in lists and tables, facilitating quick scanning of expenses.

- **Monetary Formatting:** Always prefix with "Rp" followed by a space. Use thousand separators (e.g., Rp 50.000).
- **Hierarchy:** Use `display-amount` for the primary account balance. Use `label-sm` for overline text and category descriptions.
- **Weight:** Reserve 600 (Semi-bold) for headings and 400 (Regular) for secondary information to maintain a light, airy feel.

## Layout & Spacing

The system follows a strict **8pt spacing rhythm**. Layouts are primarily **Fluid**, designed for mobile-first, one-handed operation.

- **Safe Zones:** Maintain a minimum 20px horizontal margin for all screen content.
- **One-Handed Operation:** Primary interaction points (Add Expense, Navigation) are clustered in the bottom 40% of the screen.
- **Grid:** Use a 4-column fluid grid for mobile. Elements like category chips should wrap naturally or exist in a horizontally scrollable row.
- **Vertical Rhythm:** Use 24px (lg) spacing between major sections and 8px (sm) between related elements within a card.

## Elevation & Depth

This design system uses **Tonal Layers** and **Ambient Shadows** to create a sense of organized calm.

- **Level 0 (Background):** #F8F9FA. The foundation.
- **Level 1 (Cards/Surfaces):** White (#FFFFFF) with a very soft, diffused shadow. Shadow: `0px 4px 20px rgba(45, 125, 154, 0.05)`. Note the subtle primary color tint in the shadow to maintain harmony.
- **Level 2 (Active Elements):** Used for items being interacted with or Bottom Sheets. Shadow: `0px 8px 30px rgba(0, 0, 0, 0.08)`.
- **Bottom Sheets:** Use a backdrop blur (12px) on the obscured content to maintain context while focusing on the task.

## Shapes

The shape language is defined by **Softness and Modernity**.

- **Standard Containers:** Cards and input fields use a 16px radius (`rounded-lg`).
- **Large Surfaces:** Bottom sheets and large dashboard containers use a 24px radius (`rounded-xl`) on top corners.
- **Small Elements:** Buttons and chips use a "Pill" style (32px+) to provide a friendly, touchable appearance that contrasts with the structured grid.
- **Icons:** Use rounded-corner iconography (e.g., Lucide or Material Symbols Rounded) to match the UI's softness.

## Components

- **Bottom Navigation:** A persistent bar using active state indicators (a soft teal pill behind the active icon). Icons are accompanied by `label-sm` text.
- **Elevated Cards:** The primary container for transaction groups. Features a 16px corner radius and the Level 1 ambient shadow.
- **Category Chips:** Pill-shaped, using a light version of the category color as a background (10% opacity) and the full-strength color for the icon and text.
- **Numeric Input:** A massive, center-aligned input for entering amounts. It uses `display-amount` typography and lacks a bottom border, instead sitting on the soft teal surface.
- **Bottom Sheets:** Used for all "Add" or "Edit" flows. They must include a "handle" indicator at the top and support "drag to dismiss."
- **Buttons:**
    - _Primary:_ Filled with #2D7D9A, white text, 56px height for accessibility.
    - _Secondary:_ Ghost style with a #2D7D9A border or light teal fill.
- **Lists:** Transaction items use a 72px height for comfortable tapping, with the amount aligned to the right using tabular figures.
