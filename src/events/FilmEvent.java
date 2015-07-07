package events;

import films.Film;


public class FilmEvent {

    private Film film;

    public FilmEvent(Film film) {
        this.film = film;
    }

    public Film getFilm() {
        return this.film;
    }

}