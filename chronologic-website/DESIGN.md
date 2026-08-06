# 🎨 Design System - Token Documentation (Shadcn UI v4)

This documentation outlines the design tokens, typography, and color palette configured via `oklch` variables within the project's CSS layer.

---

## 🏗️ Base Structure & Typography

The project relies on clean, modern typefaces with a technical and minimalist aesthetic.

*   **Default Typography (`html`)**: `JetBrains Mono Variable` (Monospace)
*   **Headings & UI Components (`--font-heading`)**: `Geist Variable` (Sans-serif)

---

## 🎨 Color Palette (CSS Tokens)

The tokens listed below control the visual theme switching seamlessly between Light and Dark mode.

### 🌓 Surface & UI Interface Colors

| Token | Light Mode | Dark Mode | Typical Application |
| :--- | :--- | :--- | :--- |
| `--background` | `oklch(1 0 0)` (White) | `oklch(0.148 0.004 228.8)` | Application main background |
| `--foreground` | `oklch(0.148 0.004 228.8)` | `oklch(0.987 0.002 197.1)` | Main text color |
| `--card` | `oklch(1 0 0)` | `oklch(0.218 0.008 223.9)` | Card component / Section backgrounds |
| `--card-foreground`| `oklch(0.148 0.004 228.8)` | `oklch(0.987 0.002 197.1)` | Text inside card components |
| `--popover` | `oklch(1 0 0)` | `oklch(0.218 0.008 223.9)` | Dropdown menus / Tooltips |
| `--popover-foreground`| `oklch(0.148 0.004 228.8)` | `oklch(0.987 0.002 197.1)` | Text inside dropdown menus |

### ⚡ Accent & Action Colors

| Token | Light Mode | Dark Mode | Typical Application |
| :--- | :--- | :--- | :--- |
| `--primary` | `oklch(0.508 0.118 165.612)` | `oklch(0.432 0.095 166.913)` | Primary buttons, active states (Greenish) |
| `--primary-foreground`| `oklch(0.979 0.021 166.113)` | `oklch(0.979 0.021 166.113)` | Text on top of primary color |
| `--secondary` | `oklch(0.967 0.001 286.375)` | `oklch(0.274 0.006 286.033)` | Secondary interactive elements |
| `--secondary-foreground`| `oklch(0.21 0.006 285.885)` | `oklch(0.985 0 0)` | Text on top of secondary color |
| `--muted` | `oklch(0.963 0.002 197.1)` | `oklch(0.275 0.011 216.9)` | Disabled or non-interactive indicators |
| `--muted-foreground` | `oklch(0.56 0.021 213.5)` | `oklch(0.723 0.014 214.4)` | Subtitles, helper text, and captions |
| `--accent` | `oklch(0.963 0.002 197.1)` | `oklch(0.275 0.011 216.9)` | Hover states on lists or navigation items |
| `--accent-foreground` | `oklch(0.218 0.008 223.9)` | `oklch(0.987 0.002 197.1)` | Text color during element hover |
| `--destructive` | `oklch(0.577 0.245 27.325)` | `oklch(0.704 0.191 22.216)` | Critical actions (Red error/deletion states) |

### 🛠️ Borders, Inputs & Focus States

| Token | Light Mode | Dark Mode | Typical Application |
| :--- | :--- | :--- | :--- |
| `--border` | `oklch(0.925 0.005 214.3)` | `oklch(1 0 0 / 10%)` | General structural borders and dividers |
| `--input` | `oklch(0.925 0.005 214.3)` | `oklch(1 0 0 / 15%)` | Form field boundaries |
| `--ring` | `oklch(0.723 0.014 214.4)` | `oklch(0.56 0.021 213.5)` | Keyboard focus rings (Accessibility) |

---

## 📊 Charts & Dashboards (`--chart-*`)

These variables ensure data visualization charts (e.g., Recharts) remain on-theme.

*   📊 **Chart 1:** `oklch(0.845 0.143 164.978)`
*   📊 **Chart 2:** `oklch(0.696 0.17 162.48)`
*   📊 **Chart 3:** `oklch(0.596 0.145 163.225)`
*   📊 **Chart 4:** `oklch(0.508 0.118 165.612)`
*   📊 **Chart 5:** `oklch(0.432 0.095 166.913)`

---

## 🗂️ Sidebar Configuration (`--sidebar-*`)

Tailored tokens mapped explicitly to customize the collapsible `Sidebar` component layout.

*   **Sidebar Base:** Light: `var(--muted)` | Dark: `oklch(0.218 0.008 223.9)`
*   **Active Item Highlight (`--sidebar-primary`):** Light: `Chart 3` | Dark: `Chart 2`

---

## 💻 Implementation Snippets (Tailwind CSS)

Quick references demonstrating how to implement these design tokens using utility classes in your components.

### 1. Textured Card
```tsx
<div className="bg-card text-card-foreground border border-border p-6 shadow-sm">
  <h2 className="font-heading text-xl font-bold tracking-tight">Feature Title</h2>
  <p className="text-muted-foreground font-mono text-sm mt-1">Status: Active</p>
</div>
```

### 2. Primary Button
```tsx
<button className="bg-primary text-primary-foreground hover:bg-primary/90 focus-visible:outline-ring px-4 py-2 text-sm font-medium transition-colors">
  Confirm Action
</button>
```

### 3. Destructive Action
```tsx
<button className="bg-destructive text-white hover:bg-destructive/90 px-4 py-2 text-sm font-medium transition-colors">
  Delete Record
</button>
```
