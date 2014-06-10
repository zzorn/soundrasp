package org.soundrasp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A configuration of module instances.
 */
public final class Patch implements Source {

    private List<Slot> slots = new ArrayList<Slot>();

    private Slot masterOutputSlot = null;

    public void addSlots(int count) {
        for (int i = 0; i < count; i++) {
            addSlot(new Slot(createNewSlotName()));
        }
    }

    public <T extends Module> T addSlotWithModule(T module) {
        addSlot(new Slot(module, createNewSlotName()));
        return module;
    }

    private void addSlot(Slot slot) {
        slots.add(slot);

        if (masterOutputSlot == null) {
            masterOutputSlot = slot;
        }
    }

    public List<Slot> getSlots() {
        return slots;
    }

    @Override
    public double getValue() {
        if (masterOutputSlot != null) return masterOutputSlot.getValue();
        else return 0;
    }

    @Override
    public String getName() {
        return "Patch " + toString();
    }

    public void update(double secondsPerSample, long sampleCounter) {
        for (Slot slot : slots) {
            slot.update(secondsPerSample, sampleCounter);
        }
    }

    private String createNewSlotName() {
        return "" + slots.size();
    }


}
