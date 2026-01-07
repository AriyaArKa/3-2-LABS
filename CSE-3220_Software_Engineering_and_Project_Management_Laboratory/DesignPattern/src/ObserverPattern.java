import java.util.ArrayList;
import java.util.List;

public class ObserverPattern {

    // ---------- Observer Interface ----------
    interface Observer {
        void update(String message);
    }

    // ---------- Subject Interface ----------
    interface Subject {
        void subscribe(Observer o);
        void unsubscribe(Observer o);
        void notifyObservers();
    }

    // ---------- Concrete Subject ----------
    static class YouTubeChannel implements Subject {
        private List<Observer> observers = new ArrayList<>();
        private String videoTitle;

        public void uploadVideo(String title) {
            this.videoTitle = title;
            notifyObservers();
        }

        @Override
        public void subscribe(Observer o) {
            observers.add(o);
        }

        @Override
        public void unsubscribe(Observer o) {
            observers.remove(o);
        }

        @Override
        public void notifyObservers() {
            for (Observer o : observers) {
                o.update(videoTitle);
            }
        }
    }

    // ---------- Concrete Observer ----------
    static class Subscriber implements Observer {
        private String name;

        public Subscriber(String name) {
            this.name = name;
        }

        @Override
        public void update(String message) {
            System.out.println(name + " notified: New video -> " + message);
        }
    }


    // ---------- Another Concrete Observer ----------
    static class EmailSubscriber implements Observer {
        private String email;

        public EmailSubscriber(String email) {
            this.email = email;
        }

        @Override
        public void update(String message) {
            System.out.println("Email sent to " + email +
                    ": New video -> " + message);
        }
    }


    // ---------- Main Method ----------
    public static void main(String[] args) {

        YouTubeChannel channel = new YouTubeChannel();

        Subscriber s1 = new Subscriber("Aman");
        Subscriber s2 = new Subscriber("Riya");

        channel.subscribe(s1);
        channel.subscribe(s2);

        channel.uploadVideo("Observer Pattern Explained!");


        EmailSubscriber e1 = new EmailSubscriber("user@gmail.com");
        channel.subscribe(e1);

        channel.uploadVideo("Loose Coupling Explained!");



    }
}
