package com.smartcommunity.admin.controller;

import com.smartcommunity.admin.common.Result;
import com.smartcommunity.admin.entity.*;
import com.smartcommunity.admin.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService service;

    @GetMapping("/notice/page")
    public Result<?> noticePage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageNotice(page, size)); }
    @PostMapping("/notice")
    public Result<Void> saveNotice(@RequestBody Notice n) { service.saveNotice(n); return Result.ok(); }
    @DeleteMapping("/notice/{id}")
    public Result<Void> delNotice(@PathVariable Integer id) { service.deleteNotice(id); return Result.ok(); }

    @GetMapping("/parking/page")
    public Result<?> parkingPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageParking(page, size)); }
    @PostMapping("/parking")
    public Result<Void> saveParking(@RequestBody Parking p) { service.saveParking(p); return Result.ok(); }
    @GetMapping("/parking/{id}/binds")
    public Result<List<ParkingBind>> parkingBinds(@PathVariable Integer id) { return Result.ok(service.getParkingBinds(id)); }

    @GetMapping("/complaint/page")
    public Result<?> complaintPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageComplaint(page, size)); }
    @PutMapping("/complaint/{id}")
    public Result<Void> complaintStatus(@PathVariable Integer id, @RequestParam Integer status)
    { service.updateComplaintStatus(id, status); return Result.ok(); }

    @GetMapping("/visitor/page")
    public Result<?> visitorPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageVisitor(page, size)); }

    @GetMapping("/charge/page")
    public Result<?> chargePage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size)
    { return Result.ok(service.pageCharge(page, size)); }
    @GetMapping("/charge/{chargeNo}/details")
    public Result<List<ChargeDetail>> chargeDetails(@PathVariable String chargeNo)
    { return Result.ok(service.getChargeDetails(chargeNo)); }
}
