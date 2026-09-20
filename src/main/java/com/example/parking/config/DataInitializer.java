package com.example.parking.config;

import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.TariffRule;
import com.example.parking.entity.User;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.enums.Role;
import com.example.parking.entity.enums.SpaceType;
import com.example.parking.entity.enums.UserCategory;
import com.example.parking.entity.enums.VehicleType;
import com.example.parking.repository.ParkingSpaceRepository;
import com.example.parking.repository.TariffRuleRepository;
import com.example.parking.repository.UserRepository;
import com.example.parking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Jeu de donnees de demarrage : comptes de chaque role, quelques vehicules,
 * le plan de places et les regles tarifaires. Ne s'execute que sur une base
 * vide (desactivable avec {@code parking.seed-demo-data: false}).
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository spaceRepository;
    private final TariffRuleRepository tariffRuleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParkingProperties properties;

    public DataInitializer(UserRepository userRepository,
                          VehicleRepository vehicleRepository,
                          ParkingSpaceRepository spaceRepository,
                          TariffRuleRepository tariffRuleRepository,
                          PasswordEncoder passwordEncoder,
                          ParkingProperties properties) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.spaceRepository = spaceRepository;
        this.tariffRuleRepository = tariffRuleRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!properties.isSeedDemoData()) {
            return;
        }
        seedUsers();
        seedSpaces();
        seedTariffRules();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        User admin = createUser("admin", "admin123", "Alice", "Rakoto", "admin@parking.local",
                Role.ADMIN, UserCategory.STANDARD);
        User manager = createUser("manager", "manager123", "Bruno", "Randria", "manager@parking.local",
                Role.MANAGER, UserCategory.STANDARD);
        User operator = createUser("operator", "operator123", "Chloe", "Rasoa", "operator@parking.local",
                Role.OPERATOR, UserCategory.STANDARD);
        User standard = createUser("client", "client123", "David", "Andria", "client@parking.local",
                Role.USER, UserCategory.STANDARD);
        User vip = createUser("vip", "vip123", "Elodie", "Ravalo", "vip@parking.local",
                Role.USER, UserCategory.VIP);
        User pmr = createUser("pmr", "pmr123", "Fabrice", "Rabe", "pmr@parking.local",
                Role.USER, UserCategory.PMR);

        userRepository.saveAll(List.of(admin, manager, operator, standard, vip, pmr));

        vehicleRepository.saveAll(List.of(
                createVehicle("1234TAA", "Toyota", "Yaris", "Blanc", VehicleType.VOITURE, standard),
                createVehicle("5678TBB", "Renault", "Kangoo", "Gris", VehicleType.UTILITAIRE, standard),
                createVehicle("9012TCC", "Mercedes", "Classe C", "Noir", VehicleType.VOITURE, vip),
                createVehicle("3456TDD", "Peugeot", "208", "Bleu", VehicleType.VOITURE, pmr),
                createVehicle("7890TEE", "Yamaha", "MT-07", "Rouge", VehicleType.MOTO, standard),
                createVehicle("2468TFF", "Tesla", "Model 3", "Blanc", VehicleType.ELECTRIQUE, manager)));

        log.info("Comptes de demonstration crees : admin/admin123, manager/manager123, "
                + "operator/operator123, client/client123, vip/vip123, pmr/pmr123");
    }

    private void seedSpaces() {
        if (spaceRepository.count() > 0) {
            return;
        }
        List<ParkingSpace> spaces = new ArrayList<>();
        // Zone A : rez-de-chaussee, places standard et PMR proches de l'entree
        for (int i = 1; i <= 12; i++) {
            spaces.add(new ParkingSpace(String.format("A%02d", i), "A", 0, SpaceType.STANDARD));
        }
        for (int i = 1; i <= 4; i++) {
            spaces.add(new ParkingSpace(String.format("AP%02d", i), "A", 0, SpaceType.PMR));
        }
        // Zone B : premier niveau, places standard et abonnes
        for (int i = 1; i <= 10; i++) {
            spaces.add(new ParkingSpace(String.format("B%02d", i), "B", 1, SpaceType.STANDARD));
        }
        for (int i = 1; i <= 6; i++) {
            spaces.add(new ParkingSpace(String.format("BA%02d", i), "B", 1, SpaceType.ABONNE));
        }
        // Zone C : niveau 2, places VIP
        for (int i = 1; i <= 6; i++) {
            spaces.add(new ParkingSpace(String.format("C%02d", i), "C", 2, SpaceType.VIP));
        }
        for (int i = 1; i <= 8; i++) {
            spaces.add(new ParkingSpace(String.format("CS%02d", i), "C", 2, SpaceType.STANDARD));
        }
        spaceRepository.saveAll(spaces);
        log.info("{} places de parking creees", spaces.size());
    }

    private void seedTariffRules() {
        if (tariffRuleRepository.count() > 0) {
            return;
        }
        ParkingProperties.Pricing pricing = properties.getPricing();

        TariffRule base = new TariffRule();
        base.setName("Tarif general");
        base.setHourlyRate(pricing.getDefaultHourlyRate());
        base.setMinimumFee(pricing.getMinimumFee());
        base.setDailyCap(pricing.getDailyCap());
        base.setOverstayMultiplier(pricing.getOverstayMultiplier());
        base.setSubscriberDiscountPercent(pricing.getSubscriberDiscountPercent());
        base.setFreeMinutes(pricing.getFreeMinutes());
        base.setPriority(0);

        TariffRule vip = new TariffRule();
        vip.setName("Zone C - places VIP");
        vip.setZone("C");
        vip.setSpaceType(SpaceType.VIP);
        vip.setHourlyRate(new BigDecimal("4000.00"));
        vip.setMinimumFee(new BigDecimal("4000.00"));
        vip.setDailyCap(new BigDecimal("40000.00"));
        vip.setOverstayMultiplier(new BigDecimal("1.75"));
        vip.setSubscriberDiscountPercent(new BigDecimal("25.00"));
        vip.setFreeMinutes(15);
        vip.setPriority(20);

        TariffRule pmr = new TariffRule();
        pmr.setName("Places PMR - tarif reduit");
        pmr.setSpaceType(SpaceType.PMR);
        pmr.setHourlyRate(new BigDecimal("1000.00"));
        pmr.setMinimumFee(new BigDecimal("500.00"));
        pmr.setDailyCap(new BigDecimal("8000.00"));
        pmr.setOverstayMultiplier(new BigDecimal("1.20"));
        pmr.setSubscriberDiscountPercent(new BigDecimal("30.00"));
        pmr.setFreeMinutes(30);
        pmr.setPriority(20);

        TariffRule moto = new TariffRule();
        moto.setName("Deux-roues");
        moto.setVehicleType(VehicleType.MOTO);
        moto.setHourlyRate(new BigDecimal("1200.00"));
        moto.setMinimumFee(new BigDecimal("600.00"));
        moto.setDailyCap(new BigDecimal("8000.00"));
        moto.setOverstayMultiplier(new BigDecimal("1.30"));
        moto.setSubscriberDiscountPercent(new BigDecimal("20.00"));
        moto.setFreeMinutes(20);
        moto.setPriority(10);

        TariffRule subscriber = new TariffRule();
        subscriber.setName("Abonnes - places dediees");
        subscriber.setSpaceType(SpaceType.ABONNE);
        subscriber.setUserCategory(UserCategory.ABONNE);
        subscriber.setHourlyRate(new BigDecimal("1500.00"));
        subscriber.setMinimumFee(BigDecimal.ZERO);
        subscriber.setDailyCap(new BigDecimal("10000.00"));
        subscriber.setOverstayMultiplier(new BigDecimal("1.25"));
        subscriber.setSubscriberDiscountPercent(new BigDecimal("50.00"));
        subscriber.setFreeMinutes(60);
        subscriber.setPriority(30);

        tariffRuleRepository.saveAll(List.of(base, vip, pmr, moto, subscriber));
        log.info("5 regles tarifaires creees");
    }

    private User createUser(String username, String rawPassword, String firstName, String lastName,
                            String email, Role role, UserCategory category) {
        User user = new User(username, passwordEncoder.encode(rawPassword), firstName, lastName, email, role);
        user.setCategory(category);
        user.setPhone("+261 34 00 000 00");
        return user;
    }

    private Vehicle createVehicle(String plate, String brand, String model, String color,
                                  VehicleType type, User owner) {
        Vehicle vehicle = new Vehicle(plate, brand, model, type, owner);
        vehicle.setColor(color);
        return vehicle;
    }
}
