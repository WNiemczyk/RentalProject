package tests;

import exceptions.EndOfShelfException;
import exceptions.FilmNotFoundException;
import exceptions.LocationIsNullException;
import films.Film;
import films.Location;
import films.Shelf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import statuses.FilmStatus;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShelfTest {

    private Shelf shelf = new Shelf();

    @Before
    public void setUp() throws Exception {
        shelf.put(new Film("Dzień świra", "Marek Koterski", 2002, FilmStatus.Available));
    }

    @Test
    public void shelfShouldNotBeEmpty() throws Exception {
        Assert.assertThat("Shelf is empty", shelf.getExistedFilms().size() > 0, is(true));
    }

    @Test
    public void findByDirector() throws FilmNotFoundException {
        String director = null;
        if (!shelf.getExistedFilms().isEmpty()) {
            for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
                director = e.getValue().getDirector();
                break;
            }
        }
        Map<Location, Film> foundFilms = shelf.findByDirector(director);

        Assert.assertThat("Films not found by director", foundFilms.isEmpty(), is(false));
    }

    @Test
    public void findByLocation() throws FilmNotFoundException {
        Location location = null;
        if (!shelf.getExistedFilms().isEmpty()) {
            for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
                location = e.getKey();
                break;
            }
        }
        Film foundFilm = shelf.findByLocation(location);

        Assert.assertNotNull("Cannot find film by location " + location, foundFilm);
    }

    @Test
    public void findLocationByDirector() throws LocationIsNullException {
        String director = null;
        if (shelf.getExistedFilms() != null) {
            for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
                director = e.getValue().getDirector();
                break;
            }
        }
        List<Location> locations = shelf.findLocationByDirector(director);

        Assert.assertThat("Locations not found by director", locations.isEmpty(), is(false));

    }

    @Test
    public void removeByLocation() throws EndOfShelfException, LocationIsNullException {
        Film film = new Film("Bracia", "Susanne Bier", 2004, FilmStatus.Available);
        int shelfSizeBefore = shelf.getExistedFilms().size();
        shelf.put(film);
        int shelfSizeAfter = shelf.getExistedFilms().size();

        Assert.assertThat("Shelf size is the same", shelfSizeBefore < shelfSizeAfter, is(true));

        Location location = null;
        for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
            if (e.getValue().getTitle().equals(film.getTitle())) {
                location = e.getKey();
            }
        }
        shelf.removeByLocation(location);
        shelfSizeAfter = shelf.getExistedFilms().size();

        Assert.assertThat("Shelf size is different", shelfSizeBefore == shelfSizeAfter, is(true));
    }

    @Test
    public void insertFilmInOccupiedLocation() throws FilmNotFoundException {
        Film film = new Film("Mulholland Drive",
                "David Lynch", 2002, FilmStatus.Available);
        String title = null;
        if (shelf.getExistedFilms() != null) {
            for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
                title = e.getValue().getTitle();
            }
        }
        shelf.insertNewFilmOnOccupiedLocation(title, film);
        for (Map.Entry<Location, Film> e : shelf.getExistedFilms().entrySet()) {
            title = e.getValue().getTitle();
            break;
        }

        Assert.assertThat("This is not expected film", title, equalTo(film.getTitle()));
    }

    @Test
    public void clearAll() {
        shelf.clearAll();
        Assert.assertThat("Shelf is not empty", shelf.getExistedFilms().size() == 0, is(true));
    }
}
