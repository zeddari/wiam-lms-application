import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICity } from '../city.model';
import { CityService } from '../service/city.service';
import { CityFormService, CityFormGroup } from './city-form.service';

@Component({
  standalone: true,
  selector: 'jhi-city-update',
  templateUrl: './city-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CityUpdateComponent implements OnInit {
  isSaving = false;
  city: ICity | null = null;

  editForm: CityFormGroup = this.cityFormService.createCityFormGroup();

  constructor(
    protected cityService: CityService,
    protected cityFormService: CityFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ city }) => {
      this.city = city;
      if (city) {
        this.updateForm(city);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const city = this.cityFormService.getCity(this.editForm);
    if (city.id !== null) {
      this.subscribeToSaveResponse(this.cityService.update(city));
    } else {
      this.subscribeToSaveResponse(this.cityService.create(city));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICity>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(city: ICity): void {
    this.city = city;
    this.cityFormService.resetForm(this.editForm, city);
  }
}
