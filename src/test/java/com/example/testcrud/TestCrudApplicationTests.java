package com.example.testcrud;

import com.example.testcrud.dto.HocSinhRequest;
import com.example.testcrud.dto.HocSinhResponse;
import com.example.testcrud.repository.HocSinhRepository;
import com.example.testcrud.service.HocSinhService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HocSinhController.class)
@Import(HocSinhService.class)
class TestCrudApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HocSinhRepository hocSinhRepository;

    @Test
    void listReturnsStudentAndClassName() throws Exception {
        when(hocSinhRepository.findAll()).thenReturn(List.of(
                new HocSinhResponse(1, "Nguyễn Văn Nam", "03/15/2009", "Nam", "Hải Phòng", 1, "10A1")
        ));

        mockMvc.perform(get("/api/hoc-sinh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hoTen").value("Nguyễn Văn Nam"))
                .andExpect(jsonPath("$[0].tenLop").value("10A1"))
                .andExpect(jsonPath("$[0].ngaySinh").value("03/15/2009"));
    }

    @Test
    void createReturnsCreatedStudent() throws Exception {
        when(hocSinhRepository.classExists(1)).thenReturn(true);
        when(hocSinhRepository.insert(any(HocSinhRequest.class), any(LocalDate.class))).thenReturn(
                new HocSinhResponse(7, "Học sinh mới", "03/15/2010", "Nam", "Hải Phòng", 1, "10A1")
        );

        mockMvc.perform(post("/api/hoc-sinh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hoTen":"Học sinh mới","ngaySinh":"03/15/2010","gioiTinh":"Nam","diaChi":"Hải Phòng","maLop":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maHS").value(7))
                .andExpect(jsonPath("$.tenLop").value("10A1"));
    }

    @Test
    void findByIdReturnsStudent() throws Exception {
        when(hocSinhRepository.findById(1)).thenReturn(Optional.of(
                new HocSinhResponse(1, "Nguyễn Văn Nam", "03/15/2009", "Nam", "Hải Phòng", 1, "10A1")
        ));

        mockMvc.perform(get("/api/hoc-sinh/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maHS").value(1))
                .andExpect(jsonPath("$.tenLop").value("10A1"));
    }

    @Test
    void updateReturnsUpdatedStudent() throws Exception {
        when(hocSinhRepository.classExists(1)).thenReturn(true);
        when(hocSinhRepository.update(org.mockito.ArgumentMatchers.eq(1), any(HocSinhRequest.class), any(LocalDate.class)))
                .thenReturn(Optional.of(new HocSinhResponse(1, "Tên mới", "03/15/2010", "Nam", "Hà Nội", 1, "10A1")));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/hoc-sinh/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hoTen":"Tên mới","ngaySinh":"03/15/2010","gioiTinh":"Nam","diaChi":"Hà Nội","maLop":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hoTen").value("Tên mới"));
    }

    @Test
    void blankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/hoc-sinh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hoTen":"   ","ngaySinh":"03/15/2010","maLop":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidCalendarDateReturnsBadRequest() throws Exception {
        when(hocSinhRepository.classExists(1)).thenReturn(true);

        mockMvc.perform(post("/api/hoc-sinh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hoTen":"Học sinh mới","ngaySinh":"02/30/2010","maLop":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dateInWrongFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/hoc-sinh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hoTen":"Học sinh mới","ngaySinh":"2010-03-15","maLop":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doReturn(true).when(hocSinhRepository).delete(1);

        mockMvc.perform(delete("/api/hoc-sinh/1"))
                .andExpect(status().isNoContent());
    }
}
