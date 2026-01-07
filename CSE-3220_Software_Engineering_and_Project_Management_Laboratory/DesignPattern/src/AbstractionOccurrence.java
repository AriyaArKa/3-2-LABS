// Abstraction–Occurrence Pattern Example
// Abstraction: Movie
// Occurrence: MovieShow

class Movie {
    private String title;
    private String director;
    private int durationMinutes;

    public Movie(String title, String director, int durationMinutes) {
        this.title = title;
        this.director = director;
        this.durationMinutes = durationMinutes;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}

class MovieShow {
    private Movie movie;      // Reference to Abstraction
    private String showTime;
    private String hall;
    private double ticketPrice;

    public MovieShow(Movie movie, String showTime, String hall, double ticketPrice) {
        this.movie = movie;
        this.showTime = showTime;
        this.hall = hall;
        this.ticketPrice = ticketPrice;
    }

    public void displayShowDetails() {
        System.out.println("Movie Title   : " + movie.getTitle());
        System.out.println("Director      : " + movie.getDirector());
        System.out.println("Duration      : " + movie.getDurationMinutes() + " minutes");
        System.out.println("Show Time     : " + showTime);
        System.out.println("Hall          : " + hall);
        System.out.println("Ticket Price  : $" + ticketPrice);
        System.out.println("------------------------------------");
    }
}

public class AbstractionOccurrence {
    public static void main(String[] args) {

        // Abstraction object (created once)
        Movie movie = new Movie("Avengers", "Russo Brothers", 180);

        // Occurrence objects (created many times)
        MovieShow show1 = new MovieShow(movie, "10:00 AM", "Hall A", 10.0);
        MovieShow show2 = new MovieShow(movie, "03:00 PM", "Hall B", 12.0);
        MovieShow show3 = new MovieShow(movie, "09:00 PM", "Hall C", 15.0);

        // Display all occurrences
        show1.displayShowDetails();
        show2.displayShowDetails();
        show3.displayShowDetails();
    }
}
