package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.controller.ItemRequestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestServiceImpl requestService;

    User user = new User(1L, "user@user.com", "user");
    Item item = new Item(1L, "Щётка для обуви", "Стандартная щётка для обуви", true, user, null);
    ItemDto itemDto = ItemMapper.toItemDto(item);
    ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Хотел бы воспользоваться щёткой для обуви", 1L, null, List.of(itemDto));

    @Test
    void whenTryUsePostMappingItemRequest_thenReturnItemRequestDto() throws Exception {
        String jackson = mapper.writeValueAsString(itemRequestDto);
        when(requestService.addItemRequest(anyLong(), any())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"));
    }

    @Test
    void whenTryUsePostMappingItemRequestWithOtherUser_thenReturnCustomException() throws Exception {
        String jackson = mapper.writeValueAsString(itemRequestDto);
        when(requestService.addItemRequest(anyLong(), any())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jackson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTryUseGetMappingAllItemRequest_thenReturnItemRequestDtoList() throws Exception {
        when(requestService.getAllItemsRequest(anyLong())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"));
    }

    @Test
    void whenTryUseGetMappingAllItemRequestWithOtherUser_thenReturnCustomException() throws Exception {
        when(requestService.getAllItemsRequest(anyLong())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTryUseGetMappingNumberItemRequest_thenReturnItemRequestDto() throws Exception {
        when(requestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.description").value("Хотел бы воспользоваться щёткой для обуви"));
    }

    @Test
    void whenTryUseGetMappingNumberItemRequestWrongParameter_thenReturnCustomException() throws Exception {
        when(requestService.getItemRequest(anyLong(), anyLong())).thenThrow(new NotFoundException("Запрос не найден."));
        mvc.perform(get("/requests/99")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetMappingNumberItemRequestWithOtherUser_thenReturnCustomException() throws Exception {
        when(requestService.getItemRequest(anyLong(), anyLong())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTryUseGetMappingAllItemRequestWithParameterAll_thenReturnItemRequestDtoList() throws Exception {

        when(requestService.getAllItemsByPage(anyLong(), any(), anyInt())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].description").value("Хотел бы воспользоваться щёткой для обуви"));
    }

    @Test
    void whenTryUseGetMappingAllItemRequestWithParameterAllWithOtherUser_thenReturnCustomException() throws Exception {
        when(requestService.getAllItemsByPage(anyLong(), any(), anyInt())).thenThrow(new NotFoundException("Пользователь не найден."));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void whenTryUseGetMappingAllItemRequestWithParameterAllWrongParameters_thenReturnCustomException() throws Exception {
        when(requestService.getAllItemsByPage(anyLong(), any(), anyInt())).thenThrow(new ValidationException("Пользователь не найден."));
        mvc.perform(get("/requests/all?from=-10&size=1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }
}
