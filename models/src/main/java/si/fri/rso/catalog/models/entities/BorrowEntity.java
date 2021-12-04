package si.fri.rso.catalog.models.entities;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="borrow")
@NamedQueries(value =
        {
                @NamedQuery(name = "Borrow.getAll", query = "SELECT b FROM BorrowEntity b"),
                @NamedQuery(name = "Borrow.getBorrowForPerson", query = "SELECT b FROM BorrowEntity b WHERE b.person = :person"),
                @NamedQuery(name = "Borrow.getBorrowForItem", query = "SELECT b FROM BorrowEntity b WHERE b.item = :item"),
                @NamedQuery(name = "Borrow.getReservedOrBorrowedItems", query = "SELECT b FROM BorrowEntity b WHERE b.reserved = true OR b.returned=false"),
                @NamedQuery(name = "Borrow.getBorrowedItems", query = "SELECT b FROM BorrowEntity b WHERE b.returned = false")

        })

public class BorrowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_date")
    private String from_date;

    @Column(name = "to_date")
    private String to_date;

    @Column(name= "reserved")
    private boolean reserved;

    @Column(name = "returned")
    private boolean returned;

    @JsonbTransient
    @ManyToOne
    @JoinColumn(name = "id_person")
    private PersonEntity person;

    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_item")
    private ItemEntity item;

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }

    public ItemEntity getItem(){
        return item;
    }

    public void setItem(ItemEntity item){
        this.item = item;
    }

    public Integer getId() {
        return id;
    }
}