package com.eu.habbo.messages.outgoing.crafting;

import com.eu.habbo.habbohotel.crafting.CraftingRecipe;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CraftableProductsComposer extends MessageComposer {
    private final List<CraftingRecipe> recipes;
    private final Collection<Item> ingredients;

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(Outgoing.craftableProductsComposer);

        this.response.appendInt(this.recipes.size());
        for (CraftingRecipe recipe : this.recipes) {
            this.response.appendString(recipe.getName());
            this.response.appendString(recipe.getReward().getName());
        }

        this.response.appendInt(this.ingredients.size());
        for (Item item : this.ingredients) {
            this.response.appendString(item.getName());
        }

        return this.response;
    }
}
