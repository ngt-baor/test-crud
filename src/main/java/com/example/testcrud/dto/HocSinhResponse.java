package com.example.testcrud.dto;

public record HocSinhResponse(
        Integer maHS,
        String hoTen,
        String ngaySinh,
        String gioiTinh,
        String diaChi,
        Integer maLop,
        String tenLop
) {
}