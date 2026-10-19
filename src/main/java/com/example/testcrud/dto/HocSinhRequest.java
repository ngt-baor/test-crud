package com.example.testcrud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record HocSinhRequest(
        @NotBlank(message = "HoTen không được để trống")
        @Size(max = 100, message = "HoTen tối đa 100 ký tự")
        String hoTen,

        @Pattern(regexp = "^$|^(0[1-9]|1[0-2])/(0[1-9]|[12][0-9]|3[01])/[0-9]{4}$",
                message = "NgaySinh phải theo định dạng MM/dd/yyyy")
        String ngaySinh,

        @Size(max = 10, message = "GioiTinh tối đa 10 ký tự")
        String gioiTinh,

        @Size(max = 200, message = "DiaChi tối đa 200 ký tự")
        String diaChi,

        @NotNull(message = "MaLop là bắt buộc")
        @Positive(message = "MaLop phải lớn hơn 0")
        Integer maLop
) {
}