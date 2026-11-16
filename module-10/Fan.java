/*
 * Rakesh Shrestha
 * CSD420-T301 Advanced Java Programming
 * Module 10.2 Programming Assignment
 * November 15, 2026
 *
 * Purpose: Fan holds one row of the "fans" table. It keeps the ID, first
 * name, last name, and favorite team together so the database class and
 * the window can pass a single object back and forth.
 *
 * Reference: The idea of a small record class with getters, setters, and
 * a toString() method comes from MyRecord.java in Professor Darrell
 * Payne's Swing_GUI_MySQL_Examples (Bellevue University).
 */
public class Fan {

    private int id;
    private String firstName;
    private String lastName;
    private String favoriteTeam;

    /**
     * Builds a fan with every field filled in.
     *
     * @param id           the fan's ID, which is the table's primary key
     * @param firstName    the fan's first name
     * @param lastName     the fan's last name
     * @param favoriteTeam the team this fan follows
     */
    public Fan(int id, String firstName, String lastName, String favoriteTeam) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.favoriteTeam = favoriteTeam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFavoriteTeam() {
        return favoriteTeam;
    }

    public void setFavoriteTeam(String favoriteTeam) {
        this.favoriteTeam = favoriteTeam;
    }

    /**
     * Returns the fan on one line, which is handy for the test output.
     */
    @Override
    public String toString() {
        return id + ", " + firstName + ", " + lastName + ", " + favoriteTeam;
    }
}
