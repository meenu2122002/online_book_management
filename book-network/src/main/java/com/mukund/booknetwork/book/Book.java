package com.mukund.booknetwork.book;

import com.mukund.booknetwork.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;


//@EqualsAndHashCode(callSuper = true)
@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Book extends BaseEntity{

    private String title;
    private String authorName;
    private String isbn;
    private  String synopsis;
    private String bookCover;
    private  boolean archived;
    private boolean shareable;



    @ManyToOne

    private User owner;



    @OneToMany(
            mappedBy = "book"
    )
    private List<Feedback>feedbacks;

    @OneToMany(
            mappedBy = "book"
    )
    private List<BookTransactionHistory>bookTransactionHistories;



    @Transient
    public double getRating(){
        if(feedbacks==null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate=this.feedbacks
                .stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate=Math.round(rate*10.0)/10.0;
        return roundedRate;
    }

}
