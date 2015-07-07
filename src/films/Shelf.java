package films;

import events.FilmEvent;
import events.FilmListener;
import exceptions.EndOfShelfException;
import exceptions.FilmNotFoundException;
import exceptions.LocationIsNullException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import statuses.FilmStatus;

import java.util.*;

public class Shelf {

    private static Logger logger = Logger.getLogger(Shelf.class);
    private final static int SHELF_HEIGHT = 9;
    private final static int SHELF_WIDTH = 5;

    private Film film;
    private List<FilmListener> filmListeners = new ArrayList<FilmListener>();

    private Map<Location, Film> existedFilms;

    public Shelf() {

        this.existedFilms = new HashMap<Location, Film>();
        PropertyConfigurator.configure("Log4J.properties");

    }

    public void init() {

        this.existedFilms.put(new Location(0, 0), new Film("La Comunidad",
                "de la Iglesia", 2004, FilmStatus.Available));
        this.existedFilms.put(new Location(1, 0), new Film("Soul Kitchen",
                "Fatih Akin", 2010, FilmStatus.Available));
        this.existedFilms.put(new Location(2, 0), new Film(
                "The Limits of Control", "Jim Jarmusch", 2009,
                FilmStatus.Available));
        this.existedFilms.put(new Location(3, 0), new Film("Broken Flowers",
                "Jim Jarmusch", 2005, FilmStatus.Available));
        this.existedFilms.put(new Location(4, 0), new Film("Dom zły",
                "Wojciech Smarzowski", 2009, FilmStatus.Available));

    }

    public synchronized void addFilmListener(FilmListener listener) {

        filmListeners.add(listener);
    }

    public synchronized void removeFilmListener(FilmListener listener) {

        filmListeners.remove(listener);
    }

    public String toString() {

        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;

        String map = "";
        Location l;

        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {

            l = e.getKey();

            if (minX > l.getX())
                minX = l.getX();
            if (maxX < l.getX())
                maxX = l.getX();
            if (minY > l.getY())
                minY = l.getY();
            if (maxY < l.getY())
                maxY = l.getY();

        }

        for (int j = maxY; j >= minY; j--) {
            for (int i = minX; i <= maxX; i++) {
                map = map + "(" + i + "," + j + ") " + "["
                        + existedFilms.get(new Location(i, j)) + "] ";
            }
            map = map + "\n";
        }

        return map;
    }

    public void show() {

        logger.info("Current films: " + this.toString());
    }

    public void put(Location location, Film film) {

        this.existedFilms.put(location, film);
        this.film = film;
        this.fireFilmAddedEvent();

        logger.info("Added film " + film + " in location " + location);
    }

    private synchronized void fireFilmAddedEvent() {

        FilmEvent event = new FilmEvent(film);
        for (FilmListener filmListener : filmListeners) {
            filmListener.filmAdded(event);
        }
    }

    public void borrow(Film film) {

        if ((film.getStatus() == FilmStatus.Available)
                || (film.getStatus() == FilmStatus.Reserved)) {
            this.film = film;
            film.setStatus(FilmStatus.Borrowed);
            fireFilmBorrowedEvent();
        }
    }

    private synchronized void fireFilmBorrowedEvent() {

        FilmEvent event = new FilmEvent(film);
        for (FilmListener filmListener : filmListeners) {
            filmListener.filmBorrowed(event);
        }
    }

    public void returnFilm(Film film) {

        this.film = film;
        film.setStatus(FilmStatus.Available);
        fireFilmReturnedEvent();
    }

    private synchronized void fireFilmReturnedEvent() {

        FilmEvent event = new FilmEvent(film);
        for (FilmListener filmListener : filmListeners) {
            filmListener.filmReturned(event);
        }
    }

    public void reserve(Film film) {

        if (film.getStatus() == FilmStatus.Available) {
            this.film = film;
            film.setStatus(FilmStatus.Reserved);
            fireFilmReservedEvent(film);
        }
    }

    private void fireFilmReservedEvent(Film film) {
        FilmEvent filmEvent = new FilmEvent(film);
        for (FilmListener filmListener : filmListeners) {
            filmListener.filmReserved(filmEvent);
        }
    }

    public Location getFreeLocation() throws EndOfShelfException {

        Location location = new Location(0, 0);

        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {

            if (location.compareTo(e.getKey()) < 1) {
                location.setX(e.getKey().getX());
                location.setY(e.getKey().getY());
            }
        }

        if ((location.getX() == Shelf.SHELF_WIDTH) && (location.getY() == Shelf.SHELF_HEIGHT))
            throw new EndOfShelfException("All shelfs are occupied");

        if (location.getX() < this.SHELF_WIDTH)
            location.setX(location.getX() + 1);
        else if (location.getX() == this.SHELF_WIDTH) {
            location.setX(0);
            location.setY(location.getY() + 1);
        }

        logger.info("Found free location: " + location);

        return location;
    }

    public void put(Film film) throws EndOfShelfException {

        Location location = this.getFreeLocation();
        if (location == null) throw new EndOfShelfException("All shelfs are occupied");
        this.put(location, film);
    }

    public void put(ArrayList<Film> films) throws EndOfShelfException {

        for (Film f : films) {
            this.put(f);
        }
    }

    public void putInLocation(Location location, Film film)
            throws LocationIsNullException {

        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {
            if ((e.getKey() != null) && (e.getKey().equals(location))) {
                throw new LocationIsNullException(
                        "Cannot put film in occupied location");
            } else if ((e.getKey() == null) && (e.getKey().equals(location))) {
                this.existedFilms.put(location, film);
            }
        }

        logger.info("Added film " + film + " in location " + location);
    }

    public void removeByLocation(Location location) throws LocationIsNullException {

        if (location == null) throw new LocationIsNullException("Cannot remove film from empty location");
        else {
            this.getExistedFilms().remove(location);
        }

        logger.info("Removed film from location " + location);
    }

    public Film findByLocation(Location location) throws FilmNotFoundException {

        Film film = this.getExistedFilms().get(location);
        if (film == null)
            throw new FilmNotFoundException("There is no film in location: "
                    + location);

        logger.info("Found film " + film + " in location" + location);

        return film;
    }

    public void insertNewFilmOnOccupiedLocation(String title, Film film) throws FilmNotFoundException {

        for (Map.Entry<Location, Film> e : this.existedFilms.entrySet()) {
            if (e.getValue().getTitle().equals(title)) {
                this.existedFilms.put(e.getKey(), film);
                break;
            } else {
                throw new FilmNotFoundException("There is not film with title"
                        + film.getTitle());
            }
        }

        logger.info("New film " + film + " is putted instead of film " + title);
    }

    public void changeLocation(Location l1, Location l2)
            throws FilmNotFoundException {

        Film location1 = this.existedFilms.get(l1);
        Film location2 = this.existedFilms.get(l2);

        if ((location1 == null) || (location2 == null)) {
            throw new FilmNotFoundException("Cannot give empty film");
        } else {
            this.existedFilms.put(l1, location2);
            this.existedFilms.put(l2, location1);
        }

        logger.info("Changed films from location " + l1 + " into " + l2
                + " and viceversa");
    }

    public void clearAll() {
        this.getExistedFilms().clear();
        logger.info("All films are cleared");
    }

    public Map<Location, Film> findByYear(int year) throws FilmNotFoundException {
        Map<Location, Film> foundedFilms = new HashMap<Location, Film>();
        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {
            if (e.getValue().getYear() == year)
                foundedFilms.put(e.getKey(), e.getValue());
        }
        if (foundedFilms.size() == 0)
            throw new FilmNotFoundException("There are not films from year: "
                    + year);

        logger.info("There are " + foundedFilms.size() + " films from year "
                + year);

        return foundedFilms;
    }

    public Map<Location, Film> findByDirector(String director)
            throws FilmNotFoundException {

        Map<Location, Film> foundedFilms = new HashMap<Location, Film>();

        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {

            if (e.getValue().getDirector().contains(director))
                foundedFilms.put(e.getKey(), e.getValue());
        }

        if (foundedFilms.size() == 0)
            throw new FilmNotFoundException("There are not films by director: "
                    + director);

        logger.info("There are " + foundedFilms.size() + " films made by " + director);

        return foundedFilms;

    }

    public List<Location> findLocationByDirector(String director)
            throws LocationIsNullException {

        List<Location> locations = new LinkedList<Location>();

        for (Map.Entry<Location, Film> e : existedFilms.entrySet()) {

            if (e.getValue().getDirector().contains(director))
                locations.add(e.getKey());
        }

        if (locations.isEmpty())
            throw new LocationIsNullException(
                    "There are not films by director: " + director);

        logger.info("Films made by " + director + " you can find at locations: ");

        for (Location location : locations) {
            System.out.println(location + ", ");
        }

        return locations;

    }

    public Map<Location, Film> getExistedFilms() {

        return existedFilms;
    }

    public void setExistedFilms(Map<Location, Film> existedFilms) {

        this.existedFilms = existedFilms;
    }

}
