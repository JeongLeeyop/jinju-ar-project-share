// package com.community.cms.api.calendar.controller;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.community.cms.api.calendar.dto.CalendarCategoryDto;
// import com.community.cms.api.calendar.service.AdmCalendarCategoryService;

// import lombok.AllArgsConstructor;

// @RestController
// @RequestMapping("/api/client/calendar/category")
// @AllArgsConstructor
// public class CalendarCategoryController {
// 	private final AdmCalendarCategoryService admCalendarCategoryService;
	
// 	@GetMapping("/list")
// 	public ResponseEntity<List<CalendarCategoryDto.detail>> listAll() {
// 		return ResponseEntity.ok(admCalendarCategoryService.listAll());
// 	}
// }
