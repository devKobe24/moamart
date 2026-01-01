package com.kobe.moamart.controller.view.admin;

import com.kobe.moamart.domain.store.entity.Store;
import com.kobe.moamart.dto.request.StoreSaveRequest;
import com.kobe.moamart.dto.response.StoreListResponse;
import com.kobe.moamart.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * packageName    : com.kobe.moamart.controller.view.admin
 * fileName       : AdminStoreViewController
 * author         : kobe
 * date           : 2025. 01. 01.
 * description    : 관리자 매장 관리 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
public class AdminStoreViewController {

    private final StoreService storeService;

    @GetMapping
    public String list(Model model) {
        List<StoreListResponse> stores = storeService.getAllStores();
        model.addAttribute("stores", stores);
        return "admin/store/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("store", new StoreSaveRequest());
        return "admin/store/form";
    }

    @PostMapping("/add")
    public String saveStore(
            @Valid @ModelAttribute("store") StoreSaveRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/store/form";
        }

        storeService.saveStore(request);
        return "redirect:/admin/stores";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Store store = storeService.getStore(id);
        
        StoreSaveRequest request = new StoreSaveRequest();
        request.setName(store.getName());
        request.setAddress(store.getAddress());
        request.setPhoneNumber(store.getPhoneNumber());
        request.setActive(store.isActive());
        
        model.addAttribute("store", request);
        model.addAttribute("storeId", id);
        return "admin/store/form";
    }

    @PostMapping("/{id}/edit")
    public String updateStore(
            @PathVariable Long id,
            @Valid @ModelAttribute("store") StoreSaveRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("storeId", id);
            return "admin/store/form";
        }

        storeService.updateStore(id, request);
        return "redirect:/admin/stores";
    }
}

