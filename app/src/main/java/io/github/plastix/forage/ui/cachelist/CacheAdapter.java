package io.github.plastix.forage.ui.cachelist;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import io.github.plastix.forage.R;
import io.github.plastix.forage.data.local.AbstractRealmAdapter;
import io.github.plastix.forage.data.local.model.Cache;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * RecyclerView adapter to get {@link Cache}s from Realm and display them.
 */
public class CacheAdapter extends AbstractRealmAdapter<Cache, CacheAdapter.CacheHolder> {

    @Inject
    public CacheAdapter(Lazy<Realm> realm) {
        super(realm);
    }

    @Override
    public CacheHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cacheView = inflater.inflate(R.layout.cache_item, parent, false);

        return new CacheHolder(cacheView);
    }

    @Override
    public void onBindViewHolder(final CacheHolder holder, int position) {
        Cache cache = getItem(position);
        Resources resources = holder.itemView.getContext().getResources();

        holder.cacheName.setText(cache.name);
        holder.cacheType.setText(resources.getString(R.string.cacheitem_type, cache.type));
        holder.cacheTerrain.setText(String.valueOf(cache.terrain));
        holder.cacheDifficulty.setText(String.valueOf(cache.difficulty));
        holder.cacheSize.setText(cache.size);

        holder.itemView.setOnClickListener(onClickListener);
    }


    /**
     * Query to get data from Realm.
     *
     * @return RealmQuery to get Realm data with.
     */
    @Override
    protected RealmQuery<Cache> getQuery() {
        return this.realm.get().where(Cache.class);
    }

    public class CacheHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cache_name)
        TextView cacheName;

        @BindView(R.id.cache_terrain)
        TextView cacheTerrain;

        @BindView(R.id.cache_difficulty)
        TextView cacheDifficulty;

        @BindView(R.id.cache_size)
        TextView cacheSize;

        @BindView(R.id.cache_type)
        TextView cacheType;

        public CacheHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
