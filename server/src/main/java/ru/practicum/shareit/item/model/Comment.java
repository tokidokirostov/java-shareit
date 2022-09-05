package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "text")
    @NotNull
    String text;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    Item item;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    User author;
    @Column(name = "created")
    LocalDateTime created;
}
