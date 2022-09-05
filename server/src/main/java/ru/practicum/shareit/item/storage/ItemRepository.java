package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long ownerId);

    Page<Item> findItemByDescriptionContainingIgnoreCaseAndIsAvailableTrueOrNameContainingIgnoreCaseAndIsAvailableTrue(String text,
                                                                                                                       String text1,
                                                                                                                       Pageable pageable);

    @Query(
            "select i from Item i where i.requestId.id=?1"
    )
    List<Item> findByRequestIdList(Long id);


}