package com.example.parking.controller.web;

import com.example.parking.dto.request.TariffRuleRequest;
import com.example.parking.dto.response.TariffRuleResponse;
import com.example.parking.mapper.TariffRuleMapper;
import com.example.parking.repository.TariffRuleRepository;
import com.example.parking.service.ParkingSpaceService;
import com.example.parking.service.TariffService;
import com.example.parking.exception.ResourceNotFoundException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

/** Configuration de la tarification (ADMIN). */
@Controller
@RequestMapping("/tariffs")
public class TariffWebController {

    private final TariffService tariffService;
    private final TariffRuleRepository tariffRuleRepository;
    private final TariffRuleMapper tariffRuleMapper;
    private final ParkingSpaceService spaceService;

    public TariffWebController(TariffService tariffService,
                              TariffRuleRepository tariffRuleRepository,
                              TariffRuleMapper tariffRuleMapper,
                              ParkingSpaceService spaceService) {
        this.tariffService = tariffService;
        this.tariffRuleRepository = tariffRuleRepository;
        this.tariffRuleMapper = tariffRuleMapper;
        this.spaceService = spaceService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 15, sort = "priority", direction = Sort.Direction.DESC)
                       Pageable pageable, Model model) {
        Page<TariffRuleResponse> page = tariffService.findAll(pageable);
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("sort", WebQueryUtils.sortParam(pageable));

        model.addAttribute("page", page);
        model.addAttribute("filterQuery", WebQueryUtils.buildFilterQuery(filters));
        model.addAttribute("activeMenu", "tariffs");
        return "tariffs/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new TariffRuleRequest());
        model.addAttribute("zones", spaceService.findZones());
        model.addAttribute("activeMenu", "tariffs");
        return "tariffs/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") TariffRuleRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("zones", spaceService.findZones());
            model.addAttribute("activeMenu", "tariffs");
            return "tariffs/form";
        }
        TariffRuleResponse created = tariffService.create(form);
        redirectAttributes.addFlashAttribute("successMessage", "Regle tarifaire " + created.name() + " creee");
        return "redirect:/tariffs";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        TariffRuleRequest form = tariffRuleMapper.toRequest(tariffRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regle tarifaire", id)));
        model.addAttribute("form", form);
        model.addAttribute("tariffId", id);
        model.addAttribute("zones", spaceService.findZones());
        model.addAttribute("activeMenu", "tariffs");
        return "tariffs/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") TariffRuleRequest form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tariffId", id);
            model.addAttribute("zones", spaceService.findZones());
            model.addAttribute("activeMenu", "tariffs");
            return "tariffs/edit";
        }
        TariffRuleResponse updated = tariffService.update(id, form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Regle tarifaire " + updated.name() + " mise a jour");
        return "redirect:/tariffs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tariffService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Regle tarifaire supprimee");
        return "redirect:/tariffs";
    }
}
