package films;

import events.FilmEvent;
import events.FilmListener;
import statuses.FilmStatus;

public class Rental implements FilmListener {

    public Shelf shelf;

    public Rental(Shelf shelf) {
        this.shelf = shelf;
    }

    public Rental() {
        super();
    }

    public void filmBorrowed(FilmEvent event) {
        event.getFilm().setStatus(FilmStatus.Available);
        System.out.println("Borrowed film: " + event.getFilm().getTitle());

    }

    public void filmReturned(FilmEvent event) {
        event.getFilm().setStatus(FilmStatus.Available);
        System.out.println("Returned film: " + event.getFilm().getTitle());
    }


    public void filmReserved(FilmEvent event) {
        event.getFilm().setStatus(FilmStatus.Reserved);
        System.out.println("Reserved film: " + event.getFilm().getTitle());
    }


    public void filmAdded(FilmEvent event) {
        event.getFilm().setStatus(FilmStatus.Available);
        System.out.println("Added film: " + event.getFilm().getTitle());
    }

}
