package films;

import exceptions.EndOfShelfException;
import exceptions.FilmNotFoundException;
import exceptions.LocationIsNullException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import statuses.FilmStatus;

import java.util.ArrayList;

/**
 * User: wojciech_niemczyk
 * Date: 03.07.15
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        PropertyConfigurator.configure("Log4J.properties");

        Rental rental = new Rental();
        Shelf shelf = new Shelf();
        shelf.addFilmListener(rental);
        shelf.init();
        shelf.show();

        try {
            shelf.putInLocation(new Location(5, 0), new Film("Bracia", "Susanne Bier", 2004, FilmStatus.Available));
        } catch (LocationIsNullException e) {
            logger.warn(e.toString());
        }

        shelf.show();

        try {
            shelf.findByLocation(new Location(1, 0));
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }

        try {
            logger.info(shelf.findByYear(2045));
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }

        try {
            logger.info(shelf.findByDirector("Jim Jarmusch"));
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }

        try {
            logger.info(shelf.findLocationByDirector("Jarmusch"));
        } catch (LocationIsNullException e) {
            logger.warn(e.toString());
        }

        try {
            logger.info(shelf.changeLocation(new Location(2, 0), new Location(3, 0)));
        } catch (LocationIsNullException e) {
            logger.warn(e.toString());
        }

        shelf.show();

        try {
            Film f = shelf.findByLocation(new Location(1, 0));
            shelf.reserve(f);
            shelf.borrow(f);
            shelf.returnFilm(f);
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }


        ArrayList<Film> newFilms = new ArrayList<Film>();
        newFilms.add(new Film("Chinatown", "Roman Polański", 1974, FilmStatus.Available));
        newFilms.add(new Film("Taxi Driver", "Martin Scorsese", 1976, FilmStatus.Available));
        newFilms.add(new Film("Snatch", "Guy Ritchie", 2000, FilmStatus.Available));
        newFilms.add(new Film("Nóż w wodzie", "Roman Polański", 1961, FilmStatus.Available));

        try {
            shelf.put(newFilms);
        } catch (EndOfShelfException e) {
            logger.warn(e.toString());
        }

        shelf.show();

        try {
            logger.info(shelf.insertNewFilmOnOccupiedLocation("La Comunidad", new Film("Mulholland Drive",
                    "David Lynch", 2002, FilmStatus.Available)));
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }

        shelf.show();

        try {
            shelf.removeByLocation(new Location(1, 0));
        } catch (LocationIsNullException e) {
            logger.warn(e.toString());
        }

        shelf.show();

        try {
            Film film = shelf.findByLocation(new Location(1, 1));
            shelf.reserve(film);
        } catch (FilmNotFoundException e) {
            logger.warn(e.toString());
        }

        shelf.clearAll();
        shelf.show();
    }
}
