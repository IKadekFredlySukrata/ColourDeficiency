# ColoDeficiency 🎨

**ColoDeficiency** is a simple Java-based application for **color vision deficiency testing and simulation**.  
It allows users to:
- Take an interactive color ordering test
- Estimate possible color vision deficiencies
- Simulate how images appear under different types of color blindness

The project is written entirely in **pure Java** using **JavaFX**, with no external image-processing libraries.

---

## ✨ Features

- 🧪 **Color Vision Test**
  - Interactive color tray sorting test
  - Inspired by hue arrangement tests (similar to Farnsworth-style logic)
  - Measures user error severity across multiple color axes

- 👁️ **Color Vision Deficiency Detection**
  - Protan (Red deficiency)
  - Deutan (Green deficiency)
  - Tritan (Blue deficiency)
  - Achromatopsia (Monochrome)

- 🎭 **Color Blindness Simulation**
  - Real-time image simulation using the **Brettel et al. color vision deficiency model**
  - Adjustable severity from normal vision to full deficiency

- 🖥️ **JavaFX GUI**
  - Click-and-swap color interaction
  - Visual feedback and cursor color tracking
  - Clean, minimal interface

---

## 🧠 How It Works (Short Explanation)

### 1. Color Test Logic
Users are shown a tray of colored tiles.  
The **first and last colors are fixed anchors**, while the middle colors are shuffled.

Users must reorder the colors into a smooth gradient:
- Each incorrect position increases the severity score
- The test runs across multiple color axes (R–G, G–B, etc.)
- Severity is normalized into a `0.0 – 1.0` range

---

### 2. Color Deficiency Detection
After all test phases:
- Protan, Deutan, and Tritan severity values are compared
- The strongest severity determines the suspected deficiency
- Mixed or indeterminate results are handled gracefully

---

### 3. Color Blindness Simulation Formula

This app uses a **linear RGB color space** and the **Brettel model** for color vision deficiency simulation.

**Pipeline:**
1. Convert sRGB → Linear RGB
2. Apply Brettel transformation matrix based on deficiency type
3. Blend original color with simulated color using severity
4. Convert Linear RGB → sRGB

Supported types:
- `protan`
- `deutan`
- `tritan`
- `achromatopsia` (monochrome)

---

## 🧮 Core Formula (Simplified)

```java
// sRGB → Linear RGB
fv < 0.04045 ? fv / 12.92 : Math.pow((fv + 0.055) / 1.055, 2.4);

// Linear RGB → sRGB
v < 0.0031308 ? v * 12.92 : 1.055 * Math.pow(v, 1/2.4) - 0.055;
