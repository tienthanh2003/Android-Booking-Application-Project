package com.example.androidbookingapplicationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// TODO: Mapbox dependency removed temporarily
// import com.mapbox.geojson.Point;
// import com.mapbox.maps.MapView;
// ... other mapbox imports

public class MapActivity extends AppCompatActivity {

    // private MapView mapView;
    private Button btnBack;
    // private PointAnnotationManager pointAnnotationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // mapView = findViewById(R.id.mapView);
        btnBack = findViewById(R.id.btnBack);

        // TODO: Implement map functionality later
        Toast.makeText(this, "Map feature will be implemented later", Toast.LENGTH_SHORT).show();

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(MapActivity.this, CustomerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    // TODO: Uncomment when Mapbox is added back
    /*
    private void initAnnotations() {
        if (pointAnnotationManager == null) {
            AnnotationPlugin annotationPlugin = (AnnotationPlugin) mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
            if (annotationPlugin != null) {
                AnnotationConfig config = new AnnotationConfig();
                pointAnnotationManager = (PointAnnotationManager)
                        annotationPlugin.createAnnotationManager(
                                com.mapbox.maps.plugin.annotation.AnnotationType.PointAnnotation,
                                config
                        );
            }
        }
    }

    private void addMarker() {
        if (pointAnnotationManager == null) return;

        Point point = Point.fromLngLat(105.8342, 21.0278); // Hà Nội
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_red);

        PointAnnotationOptions options = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(icon)
                .withIconSize(0.4f);

        pointAnnotationManager.create(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    */
}
