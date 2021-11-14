package si.fri.rso.catalog.models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "person")
@NamedQueries(value =
        {
                @NamedQuery(name = "Person.getAll", query = "SELECT p FROM PersonEntity p")

        })
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<BorrowEntity> getBorrows() {
        return borrows;
    }

    public void setBorrows(List<BorrowEntity> borrows) {
        this.borrows = borrows;
    }

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @OneToMany(fetch = FetchType.EAGER)
    private List<BorrowEntity> borrows;


    public Integer getId() {
        return id;
    }
}
