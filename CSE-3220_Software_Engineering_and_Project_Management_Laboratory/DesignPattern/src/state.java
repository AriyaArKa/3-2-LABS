// ===== STATE INTERFACE =====
interface Tool {
    void onClick();
}

// ===== CONCRETE STATES =====
class BrushTool implements Tool {
    @Override
    public void onClick() {
        System.out.println("Drawing with Brush Tool");
    }
}

class EraserTool implements Tool {
    @Override
    public void onClick() {
        System.out.println("Erasing with Eraser Tool");
    }
}

class SelectionTool implements Tool {
    @Override
    public void onClick() {
        System.out.println("Selecting an object");
    }
}

// ===== CONTEXT CLASS =====
class Canvas {

    private Tool currentTool;

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    public void click() {
        currentTool.onClick();
    }
}

// ===== MAIN CLASS =====
public class state {
    public static void main(String[] args) {

        Canvas canvas = new Canvas();

        canvas.setTool(new BrushTool());
        canvas.click();

        canvas.setTool(new EraserTool());
        canvas.click();

        canvas.setTool(new SelectionTool());
        canvas.click();
    }
}
