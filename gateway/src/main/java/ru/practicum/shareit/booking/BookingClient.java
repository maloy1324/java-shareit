package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getUserBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createBooking(Long bookerId, BookingDTO bookingDTO) {
        String url = "";
        return post(url, bookerId, bookingDTO);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookerId) {
        String url = "/" + bookerId;
        return get(url, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner" + "?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> updateBookingStatus(long bookerId, long bookingId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        String url = "/" + bookingId + "?approved=" + approved;
        return patch(url, bookerId, parameters);
    }
}
