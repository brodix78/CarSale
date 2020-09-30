package ru.carSale.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.json.JSONObject;
import ru.carSale.model.*;
import ru.carSale.model.car.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class Store implements AutoCloseable{

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sF = new MetadataSources(registry).buildMetadata()
            .buildSessionFactory();

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sF.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public List<Advert> allAdverts(int start, int numbers) {
        return this.tx(session -> session.createQuery("FROM ru.carSale.model.Advert WHERE sold=false ORDER BY date DESC")
                .setFirstResult(start).setMaxResults(numbers).list());
    }

    public List<Advert> allAdvertsArchive(int start, int numbers) {
        return this.tx(session -> session.createQuery("FROM ru.carSale.model.Advert ORDER BY id")
                .setFirstResult(start).setMaxResults(numbers).list());
    }

    public List<Advert> advertsByCustomer(int id, int start, int numbers) {
        return this.tx(session -> session.createQuery("FROM ru.carSale.model.Advert WHERE customer_id=?1 ORDER BY date DESC")
                .setParameter(1, id).setFirstResult(start).setMaxResults(numbers).list());
    }

    public Advert advertById(int id) {
        return this.tx(session -> session.get(Advert.class, id));
    }

    public List<Advert> advertsByFilter(Filter filter) {
        int start = filter.getFirstAdvert();
        int max = filter.getMaxSize();
        if (filter.isSold() && filter.getCustomerId() == 0) {
            return allAdvertsArchive(start, max);
        } else if (!filter.isSold() && filter.getCustomerId() == 0) {
            return allAdverts(start, max);
        } else if (filter.getCustomerId() > 0) {
            return advertsByCustomer(filter.getCustomerId(), start, max);
        } else {
            return new ArrayList<>();
        }
    }

    public Advert saveAdvert(Advert advert) {
        return this.tx(session -> {
            session.saveOrUpdate(advert);
            return advert;
        });
    }

    public List<Brand> getLazyBrands() {
        return this.tx(session -> {
            List<Brand> brands = session.createQuery("FROM ru.carSale.model.car.Brand").list();
            brands.stream().forEach(brand -> brand.setModels(new HashSet<>()));
            return brands;
        });
    }

    public List<Brand> getBrands() {
        return this.tx(session -> {
            List<Brand> brands = session.createQuery("FROM ru.carSale.model.car.Brand").list();
            brands.stream().forEach(brand -> brand.getModels().size());
            return brands;
        });
    }

    public Brand brandById(int id) {
        return this.tx(session -> {
            Brand brand = session.get(Brand.class, id);
            brand.getModels().size();
            return brand;
        });
    }

    public Model modelById(int id) {
        return this.tx(session -> {
            Model model = session.get(Model.class, id);
            model.getGenerations().size();
            return model;
        });
    }

    public Generation generationById(int id) {
        return this.tx(session -> {
            Generation gen = session.get(Generation.class, id);
            gen.getConfigs().size();
            return gen;
        });
    }

    public Config configById(int id) {
        return this.tx(session -> session.get(Config.class, id));
    }

    public Photo savePhoto(Photo photo) {
        return this.tx(session -> {
            session.saveOrUpdate(photo);
            return photo;
        });
    }

    public Customer saveCustomer(Customer customer) {
        return this.tx(session -> {
            Passport passport = Passport.of(customer.getLogin(), new String(customer.getPassword()));
            customer.setId(0);
            customer.setPassword("");
            session.save(customer);
            if (customer.getId() > 0) {
                session.save(passport);
            }
            return customer;
        });
    }

    public Customer getAuth(Passport passport) {
        return this.tx(session -> {
            Customer customer = Customer.emptyCustomer();
            List rsl = new ArrayList();
            if (passport.equals(session.get(Passport.class, passport.getLogin()))) {
                rsl = session.createQuery("FROM ru.carSale.model.Customer WHERE login=?1")
                        .setParameter(1, passport.getLogin()).list();
            }
            if (rsl.size() == 1) {
                for(Object one : rsl) {
                    customer = (Customer) one;
                    customer.getAdverts().size();
                }
            }
            return customer;
        });
    }

    public boolean checkCustomer(Customer customer) {
        return this.tx(session -> customer.equals(session.get(Customer.class, customer.getId())));
    }

    public static void main22(String[] args){
        Store store = new Store();
        Body sed = Body.of("Седан");
        store.checkAndSave(sed);
        Body uni = Body.of("Универсал");
        store.checkAndSave(uni);
        Body h3 = Body.of("Хэтчбек 3дв.");
        store.checkAndSave(h3);
        Fuel gasoline = Fuel.of("Бензин");
        store.checkAndSave(gasoline);
        Fuel disel = Fuel.of("Дизель");
        store.checkAndSave(disel);
        Transmission man = Transmission.of("Механика");
        store.checkAndSave(man);
        Transmission auto = Transmission.of("АКПП");
        store.checkAndSave(auto);
        Engine four16 = Engine.of("4R16", gasoline, 1.6, 115);
        store.checkAndSave(four16);
        Engine four18 = Engine.of("4R18", gasoline, 1.8, 135);
        store.checkAndSave(four18);
        Engine six25 = Engine.of("6V25", disel, 2.5, 170);
        store.checkAndSave(six25);
        Brand first = Brand.of("First");
        Brand fast = Brand.of("Fast");
        store.checkAndSave(first);
        store.checkAndSave(fast);
        Model cityCar = Model.of("CityCar", first);
        first.addModel(cityCar);
        store.checkAndSave(cityCar);
            Generation aGen = Generation.of("1", cityCar, 1995, 2000);
            store.checkAndSave(aGen);
            cityCar.addGeneration(aGen);
                Config aaConf = Config.of(aGen, h3, four16, man);
                store.checkAndSave(aaConf);
                aGen.addConfig(aaConf);
                Config abConf = Config.of(aGen, h3, four18, auto);
                store.checkAndSave(abConf);
                aGen.addConfig(abConf);
                store.checkAndSave(aGen);
            Generation bGen = Generation.of("2", cityCar, 2000, 2005);
            store.checkAndSave(bGen);
            cityCar.addGeneration(bGen);
                Config baConf = Config.of(bGen, h3, four16, man);
                store.checkAndSave(baConf);
                bGen.addConfig(baConf);
                Config bbConf = Config.of(bGen, h3, four18, auto);
                store.checkAndSave(bbConf);
                bGen.addConfig(bbConf);
                store.checkAndSave(bGen);
            Generation cGen = Generation.of("3", cityCar, 2005, 2010);
            store.checkAndSave(cGen);
            cityCar.addGeneration(cGen);
                Config caConf = Config.of(cGen, h3, four16, man);
                store.checkAndSave(caConf);
                cGen.addConfig(caConf);
                Config cbConf = Config.of(cGen, h3, four18, auto);
                store.checkAndSave(cbConf);
                cGen.addConfig(cbConf);
                store.checkAndSave(cGen);
                store.checkAndSave(cityCar);
        Model hwCar = Model.of("HighwayCar", first);
        store.checkAndSave(hwCar);
        first.addModel(hwCar);
            Generation haGen = Generation.of("1", hwCar, 1995, 2000);
            store.checkAndSave(haGen);
            hwCar.addGeneration(haGen);
                Config haaConf = Config.of(haGen, sed, four18, man);
                store.checkAndSave(haaConf);
                haGen.addConfig(haaConf);
                Config habConf = Config.of(haGen, uni, six25, auto);
                store.checkAndSave(habConf);
                haGen.addConfig(habConf);
                store.checkAndSave(haGen);
            Generation hbGen = Generation.of("2", hwCar, 2000, 2005);
            store.checkAndSave(hbGen);
            hwCar.addGeneration(hbGen);
                Config hbaConf = Config.of(hbGen, sed, four18, man);
                store.checkAndSave(hbaConf);
                hbGen.addConfig(hbaConf);
                Config hbbConf = Config.of(hbGen, uni, six25, auto);
                store.checkAndSave(hbbConf);
                hbGen.addConfig(hbbConf);
                store.checkAndSave(hbGen);
            Generation hcGen = Generation.of("3", hwCar, 2005, 2010);
            store.checkAndSave(hcGen);
            hwCar.addGeneration(hcGen);
                Config hcaConf = Config.of(hcGen, sed, four18, man);
                store.checkAndSave(hcaConf);
                hcGen.addConfig(hcaConf);
                Config hcbConf = Config.of(hcGen, uni, six25, auto);
                store.checkAndSave(hcbConf);
                hcGen.addConfig(hcbConf);
                store.checkAndSave(hcGen);
                store.checkAndSave(hwCar);
        store.checkAndSave(first);
        int id = 1;
        Brand br = store.brandById(id).forJson();
        System.out.println("BRAND:  " + br.toString());
        System.out.println("MODELS:  " + br.getModels().toString());
        String json = new JSONObject(br).toString();
        System.out.println("JSON:  " + json);
        }

    private <T> void checkAndSave(T data) {
        this.tx(session -> {
            session.saveOrUpdate(data);
            return data;
        });
    }

    public static void main(String[] args) {
        Store store = new Store();
        Photo photo = Photo.of("images/emoji-smiley-emoticon-sticker-smiley.jpg");
        System.out.println(store.savePhoto(photo).getFile());
    }
}