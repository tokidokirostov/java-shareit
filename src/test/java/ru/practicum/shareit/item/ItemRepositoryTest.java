package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    ItemRepository itemRepository;

    @Test
    void hren() {
        User user = new User(null, "user@user.com", "user");
        ItemRequest itemRequest1 = new ItemRequest(null, "Хотел бы воспользоваться щёткой для обуви", user, null);
        Item item = new Item(null, "Щётка для обуви", "Стандартная щётка для обуви", true, user, itemRequest1);
        Item item2 = new Item(null, "Щётка для одежды", "Стандартная щётка для одежды",
                true, user, itemRequest1);
        this.em.persist(user);
        em.persist(itemRequest1);
        em.persist(item);
        em.persist(item2);
        List<Item> result = itemRepository.findByRequestIdList(1L);
        assertEquals(2, result.size());
        assertEquals(item, result.get(0));
        assertEquals(item2, result.get(1));
    }

}
