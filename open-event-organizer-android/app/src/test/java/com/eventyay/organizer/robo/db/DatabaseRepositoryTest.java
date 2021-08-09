package com.eventyay.organizer.robo.db;

import com.eventyay.organizer.common.model.SimpleModel;
import com.eventyay.organizer.common.model.SimpleModel_Table;
import com.eventyay.organizer.data.db.DbFlowDatabaseRepository;
import com.eventyay.organizer.data.user.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRepositoryTest extends BaseTest {

    static final SimpleModel MODEL = new SimpleModel(1, "Title", "Greta Jones");
    private static final SimpleModel MODEL1 = new SimpleModel(2, "Second Title", "Bob Parsley");

    private static final List<SimpleModel> LIST = new ArrayList<>();

    static {
        LIST.add(MODEL);
        LIST.add(MODEL1);
    }

    private DbFlowDatabaseRepository databaseRepository;

    @Override
    public void setUp() {
        databaseRepository = new DbFlowDatabaseRepository();
    }

    @Test
    public void testBasicSaveAndRead() {
        databaseRepository
            .save(SimpleModel.class, MODEL)
            .subscribe();

        databaseRepository.getItems(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertSubscribed()
            .assertValue(savedModel -> savedModel.equals(MODEL));
    }

    @Test
    public void testBasicDelete() {
        databaseRepository
            .save(SimpleModel.class, MODEL)
            .subscribe();

        databaseRepository
            .delete(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .subscribe();

        databaseRepository.getItems(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertNoValues();
    }

    @Test
    public void testListSave() {
        databaseRepository
            .saveList(SimpleModel.class, LIST)
            .subscribe();

        databaseRepository
            .getAllItems(SimpleModel.class)
            .test()
            .assertValueSequence(LIST);
    }

    @Test
    public void testListDelete() {
        databaseRepository
            .saveList(SimpleModel.class, LIST)
            .subscribe();

        databaseRepository
            .deleteAll(SimpleModel.class)
            .test()
            .assertNoValues();
    }

    @Test
    public void testMultipleDeleteAll() {
        databaseRepository
            .saveList(SimpleModel.class, LIST)
            .subscribe();

        databaseRepository
            .save(User.class, new User())
            .subscribe();

        databaseRepository
            .deleteAll(User.class, SimpleModel.class)
            .subscribe();

        databaseRepository
            .getAllItems(User.class)
            .test()
            .assertNoValues();

        databaseRepository
            .getAllItems(SimpleModel.class)
            .test()
            .assertNoValues();
    }

    @Test
    public void testUpdate() {
        databaseRepository
            .save(SimpleModel.class, MODEL)
            .subscribe();

        SimpleModel newModel = SimpleModel.fromModel(MODEL);
        newModel.setDescription("Melshi Jones");

        databaseRepository
            .update(SimpleModel.class, newModel)
            .subscribe();

        databaseRepository
            .getItems(SimpleModel.class, SimpleModel_Table.id.eq(1L))
            .test()
            .assertValue(simpleModel -> simpleModel.getDescription().equals("Melshi Jones"));
    }
}
