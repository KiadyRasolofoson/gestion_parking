package com.example.parking.controller.web;

import com.example.parking.dto.request.CreateUserRequest;
import com.example.parking.dto.request.UpdateUserRequest;
import com.example.parking.dto.response.UserResponse;
import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.mapper.UserMapper;
import com.example.parking.service.UserService;
import com.example.parking.util.WebQueryUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserWebController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) Role role,
                       @RequestParam(required = false) UserCategory category,
                       @RequestParam(required = false) Boolean enabled,
                       @PageableDefault(size = 15, sort = "lastName", direction = Sort.Direction.ASC)
                       Pageable pageable,
                       Model model) {
        Page<UserResponse> page = userService.search(search, role, category, enabled, pageable);

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("search", search);
        filters.put("role", role);
        filters.put("category", category);
        filters.put("enabled", enabled);
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("role", role);
        model.addAttribute("category", category);
        model.addAttribute("enabled", enabled);
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "users");
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CreateUserRequest());
        model.addAttribute("activeMenu", "users");
        return "users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CreateUserRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "users");
            return "users/form";
        }
        UserResponse created = userService.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Compte " + created.username() + " cree (" + created.role().getLabel() + ")");
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", userMapper.toUpdateRequest(userService.getEntity(id)));
        model.addAttribute("userId", id);
        model.addAttribute("activeMenu", "users");
        return "users/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") UpdateUserRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("activeMenu", "users");
            return "users/edit";
        }
        UserResponse updated = userService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Compte " + updated.username() + " mis a jour");
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Compte supprime");
        return "redirect:/users";
    }
}
