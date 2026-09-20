package com.example.parking.controller.web;

import com.example.parking.dto.request.CreateSpaceRequest;
import com.example.parking.dto.request.UpdateSpaceRequest;
import com.example.parking.dto.response.ParkingSpaceResponse;
import com.example.parking.entity.enums.SpaceStatus;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.mapper.ParkingSpaceMapper;
import com.example.parking.service.ParkingSpaceService;
import com.example.parking.service.StatisticsService;
import com.example.parking.util.WebQueryUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/spaces")
public class SpaceWebController {

    private final ParkingSpaceService spaceService;
    private final StatisticsService statisticsService;
    private final ParkingSpaceMapper spaceMapper;

    public SpaceWebController(ParkingSpaceService spaceService,
                              StatisticsService statisticsService,
                              ParkingSpaceMapper spaceMapper) {
        this.spaceService = spaceService;
        this.statisticsService = statisticsService;
        this.spaceMapper = spaceMapper;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String zone,
                       @RequestParam(required = false) SpaceType type,
                       @RequestParam(required = false) SpaceStatus status,
                       @RequestParam(required = false) Integer floor,
                       @PageableDefault(size = 15, sort = {"zone", "number"}, direction = Sort.Direction.ASC)
                       Pageable pageable,
                       Model model) {
        Page<ParkingSpaceResponse> page = spaceService.search(search, zone, type, status, floor, pageable);
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("search", search);
        filters.put("zone", zone);
        filters.put("type", type);
        filters.put("status", status);
        filters.put("floor", floor);
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("search", search);
        model.addAttribute("zone", zone);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("floor", floor);
        model.addAttribute("zones", spaceService.findZones());
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "spaces");
        return "spaces/list";
    }

    @GetMapping("/map")
    public String map(Model model) {
        model.addAttribute("zonedSpaces", spaceService.findGroupedByZone());
        model.addAttribute("occupancy", statisticsService.occupancy());
        model.addAttribute("activeMenu", "map");
        return "spaces/map";
    }

    @GetMapping("/available")
    public String available(@RequestParam(required = false) SpaceType type,
                            @RequestParam(required = false) String zone,
                            Model model) {
        model.addAttribute("spaces", spaceService.findAvailable(type, zone));
        model.addAttribute("zones", spaceService.findZones());
        model.addAttribute("type", type);
        model.addAttribute("zone", zone);
        model.addAttribute("activeMenu", "available");
        return "spaces/available";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("form", new CreateSpaceRequest());
        model.addAttribute("activeMenu", "spaces");
        return "spaces/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("form") CreateSpaceRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "spaces");
            return "spaces/form";
        }
        ParkingSpaceResponse created = spaceService.create(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Place " + created.number() + " creee avec succes");
        return "redirect:/spaces";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", spaceMapper.toUpdateRequest(spaceService.getEntity(id)));
        model.addAttribute("spaceId", id);
        model.addAttribute("activeMenu", "spaces");
        return "spaces/edit";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") UpdateSpaceRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("spaceId", id);
            model.addAttribute("activeMenu", "spaces");
            return "spaces/edit";
        }
        ParkingSpaceResponse updated = spaceService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Place " + updated.number() + " mise a jour");
        return "redirect:/spaces";
    }

    @PostMapping("/{id}/out-of-service")
    @PreAuthorize("hasRole('ADMIN')")
    public String outOfService(@PathVariable Long id,
                               @RequestParam(required = false) String reason,
                               RedirectAttributes redirectAttributes) {
        ParkingSpaceResponse updated = spaceService.setOutOfService(id, reason);
        redirectAttributes.addFlashAttribute("successMessage",
                "Place " + updated.number() + " mise hors service");
        return "redirect:/spaces";
    }

    @PostMapping("/{id}/in-service")
    @PreAuthorize("hasRole('ADMIN')")
    public String inService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ParkingSpaceResponse updated = spaceService.setInService(id);
        redirectAttributes.addFlashAttribute("successMessage", "Place " + updated.number() + " remise en service");
        return "redirect:/spaces";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        spaceService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Place supprimee");
        return "redirect:/spaces";
    }
}
