package com.example.parking.controller.web;

import com.example.parking.dto.request.CreateVehicleRequest;
import com.example.parking.dto.request.UpdateVehicleRequest;
import com.example.parking.dto.response.VehicleResponse;
import com.example.parking.entity.enums.VehicleType;
import com.example.parking.mapper.VehicleMapper;
import com.example.parking.service.UserService;
import com.example.parking.service.VehicleService;
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
@RequestMapping("/vehicles")
public class VehicleWebController {

    private final VehicleService vehicleService;
    private final UserService userService;
    private final VehicleMapper vehicleMapper;

    public VehicleWebController(VehicleService vehicleService,
                                UserService userService,
                                VehicleMapper vehicleMapper) {
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.vehicleMapper = vehicleMapper;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) VehicleType type,
                       @RequestParam(required = false) Long ownerId,
                       @PageableDefault(size = 15, sort = "plateNumber", direction = Sort.Direction.ASC)
                       Pageable pageable,
                       Model model) {
        Page<VehicleResponse> page = vehicleService.search(search, type, ownerId, pageable);

        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("search", search);
        filters.put("type", type);
        filters.put("ownerId", ownerId);
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "vehicles");
        return "vehicles/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CreateVehicleRequest());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("activeMenu", "vehicles");
        return "vehicles/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") CreateVehicleRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.findAll());
            model.addAttribute("activeMenu", "vehicles");
            return "vehicles/form";
        }
        VehicleResponse created = vehicleService.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Vehicule " + created.plateNumber() + " enregistre");
        return "redirect:/vehicles";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", vehicleMapper.toUpdateRequest(vehicleService.getEntity(id)));
        model.addAttribute("vehicleId", id);
        model.addAttribute("users", userService.findAll());
        model.addAttribute("activeMenu", "vehicles");
        return "vehicles/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") UpdateVehicleRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("activeMenu", "vehicles");
            return "vehicles/edit";
        }
        VehicleResponse updated = vehicleService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Vehicule " + updated.plateNumber() + " mis a jour");
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicule supprime");
        return "redirect:/vehicles";
    }
}
