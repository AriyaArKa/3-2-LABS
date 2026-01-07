import java.util.Stack;

// ===== MEMENTO =====
class CanvasMemento {
    private final String drawingState;

    public CanvasMemento(String drawingState) {
        this.drawingState = drawingState;
    }

    public String getState() {
        return drawingState;
    }
}

// ===== ORIGINATOR =====
class Canvas1 {

    private String drawing;

    public void draw(String action) {
        drawing = action;
    }

    public String getDrawing() {
        return drawing;
    }

    public CanvasMemento save() {
        return new CanvasMemento(drawing);
    }

    public void restore(CanvasMemento memento) {
        drawing = memento.getState();
    }
}

// ===== CARETAKER =====
class History {
    private Stack<CanvasMemento> history = new Stack<>();

    public void save(CanvasMemento memento) {
        history.push(memento);
    }

    public CanvasMemento undo() {
        return history.pop();
    }
}

// ===== MAIN CLASS =====
public class UndoPattern {
    public static void main(String[] args) {

        Canvas1 canvas = new Canvas1();
        History history = new History();

        canvas.draw("Draw Circle");
        history.save(canvas.save());

        canvas.draw("Draw Square");
        history.save(canvas.save());

        canvas.draw("Draw Triangle");
        System.out.println("Current Drawing: " + canvas.getDrawing());

        canvas.restore(history.undo());
        System.out.println("After Undo: " + canvas.getDrawing());

        canvas.restore(history.undo());
        System.out.println("After Second Undo: " + canvas.getDrawing());
    }
}
