package models;
import java.util.UUID;

@SuppressWarnings("unused")

public class User {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String bio;
    private final String imageUrl;

    public User(String firstName, String lastName, String bio, String imageUrl) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }
}