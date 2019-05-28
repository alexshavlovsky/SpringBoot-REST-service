package com.ctzn.mynotesservice.components;

import com.ctzn.mynotesservice.model.notebook.NotebookEntity;
import com.ctzn.mynotesservice.model.note.NoteEntity;
import com.ctzn.mynotesservice.repositories.NoteRepository;
import com.ctzn.mynotesservice.repositories.NotebookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

@Component
public class DbSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DbSeeder.class);

    private NotebookRepository notebookRepository;
    private NoteRepository noteRepository;

    public DbSeeder(NotebookRepository notebookRepository, NoteRepository noteRepository) {
        this.notebookRepository = notebookRepository;
        this.noteRepository = noteRepository;
    }

    private void generateRandomDb(int nbNum, int notesPerNbMin, int notesPerNbMax) {
        notebookRepository.deleteAll();
        Random rnd = new Random();
        long now = new Date().getTime();
        for (int i = 0; i < nbNum; i++) {
            NotebookEntity nb = new NotebookEntity("Notebook " + (i + 1));
            notebookRepository.save(nb);
            int notesNum = rnd.nextInt(notesPerNbMax - notesPerNbMin + 1) + notesPerNbMin;
            for (int n = 0; n < notesNum; n++) {
                NoteEntity note = new NoteEntity(
                        "Note " + (i + 1) + "." + (n + 1),
                        NonsenseGenerator.getInstance().makeText(1),
                        nb);
                // set random creation date within last year
                int period = (int) Duration.ofDays(365L).getSeconds();
                note.setLastModifiedOn(new Date(now - 1000L * rnd.nextInt(period)));
                noteRepository.save(note);
            }
        }
    }

    @Override
    public void run(String... args) {
        // put random data to db if db is empty
        if (notebookRepository.count() == 0 || (args.length == 1 && "force".equals(args[0]))) {
            generateRandomDb(12, 3, 40);
            logger.debug("Random database generated");
        }
    }
}