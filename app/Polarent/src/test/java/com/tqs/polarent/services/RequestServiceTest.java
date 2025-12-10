package com.tqs.polarent.services;

import com.tqs.polarent.dto.RequestRequestDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Request;
import com.tqs.polarent.mapper.RequestMapper;
import com.tqs.polarent.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestService requestService;

    private Request request;
    private RequestResponseDTO requestResponseDTO;

    @BeforeEach
    void setUp() {
        request = Request.builder()
                .id(1L)
                .listingId(10L)
                .requesterId(5L)
                .initialDate(20251210)
                .duration(3)
                .build();

        requestResponseDTO = new RequestResponseDTO();
        requestResponseDTO.setId(1L);
        requestResponseDTO.setListingId(10L);
        requestResponseDTO.setRequesterId(5L);
        requestResponseDTO.setInitialDate(20251210);
        requestResponseDTO.setDuration(3);
    }

    @Test
    void whenGetRequestsByListing_thenReturnList() {
        when(requestRepository.findByListingId(10L)).thenReturn(List.of(request));
        when(requestMapper.toDto(request)).thenReturn(requestResponseDTO);

        List<RequestResponseDTO> result = requestService.getRequestsByListing(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getListingId()).isEqualTo(10L);
    }

    @Test
    void whenGetRequestsByListing_withNoRequests_thenReturnEmptyList() {
        when(requestRepository.findByListingId(99L)).thenReturn(List.of());

        List<RequestResponseDTO> result = requestService.getRequestsByListing(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void whenGetRequestsByListingAndRequester_thenReturnList() {
        when(requestRepository.findByListingIdAndRequesterId(10L, 5L)).thenReturn(List.of(request));
        when(requestMapper.toDto(request)).thenReturn(requestResponseDTO);

        List<RequestResponseDTO> result = requestService.getRequestsByListingAndRequester(10L, 5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRequesterId()).isEqualTo(5L);
    }

    @Test
    void whenDeleteRequest_withValidId_thenDeleteSuccessfully() {
        when(requestRepository.existsById(1L)).thenReturn(true);
        doNothing().when(requestRepository).deleteById(1L);

        requestService.deleteRequest(1L);

        verify(requestRepository).deleteById(1L);
    }

    @Test
    void whenDeleteRequest_withInvalidId_thenThrowException() {
        when(requestRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> requestService.deleteRequest(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request not found");
    }

    @Test
    void whenCreateRequest_thenReturnCreatedRequest() {
        RequestRequestDTO dto = new RequestRequestDTO();
        dto.setListingId(10L);
        dto.setRequesterId(5L);
        dto.setInitialDate(20251210);
        dto.setDuration(3);

        when(requestMapper.toEntity(dto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(requestResponseDTO);

        RequestResponseDTO result = requestService.createRequest(dto);

        assertThat(result.getListingId()).isEqualTo(10L);
        verify(requestRepository).save(request);
    }
}
